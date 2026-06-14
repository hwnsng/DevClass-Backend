package com.hwnsng.devclass.question.service;

import com.hwnsng.devclass.common.exception.CustomException;
import com.hwnsng.devclass.course.entity.Course;
import com.hwnsng.devclass.course.repository.CourseRepository;
import com.hwnsng.devclass.enrollment.entity.Enrollment;
import com.hwnsng.devclass.enrollment.repository.EnrollmentRepository;
import com.hwnsng.devclass.notification.service.NotificationService;
import com.hwnsng.devclass.question.dto.QuestionAnswerRequest;
import com.hwnsng.devclass.question.dto.QuestionCreateRequest;
import com.hwnsng.devclass.question.dto.QuestionResponse;
import com.hwnsng.devclass.question.entity.CourseQuestion;
import com.hwnsng.devclass.question.repository.CourseQuestionRepository;
import com.hwnsng.devclass.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseQuestionService {

    private final CourseQuestionRepository questionRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public QuestionResponse create(Long courseId, Long userId, QuestionCreateRequest request) {
        Course course = getCourse(courseId);
        boolean enrolled = enrollmentRepository.existsByUserIdAndCourseIdAndStatus(
                userId, courseId, Enrollment.EnrollmentStatus.ENROLLED);
        if (!enrolled) {
            throw new CustomException(HttpStatus.FORBIDDEN, "NOT_ENROLLED", "수강 중인 학생만 질문할 수 있습니다.");
        }
        CourseQuestion saved = questionRepository.save(CourseQuestion.create(
                courseId, userId, request.title().trim(), request.content().trim()));
        notificationService.send(course.getInstructorId(), "새 강의 질문", course.getTitle() + " 강의에 새 질문이 등록되었습니다.");
        return toResponse(saved);
    }

    public Page<QuestionResponse> getCourseQuestions(Long courseId, int page, int size) {
        getCourse(courseId);
        return questionRepository.findByCourseIdOrderByCreatedAtDesc(courseId, PageRequest.of(page - 1, size))
                .map(this::toResponse);
    }

    public Page<QuestionResponse> getInstructorQuestions(Long instructorId, Long courseId, int page, int size) {
        PageRequest pageable = PageRequest.of(page - 1, size);
        if (courseId != null) {
            Course course = getCourse(courseId);
            verifyOwner(course, instructorId);
            return questionRepository.findByCourseIdOrderByCreatedAtDesc(courseId, pageable).map(this::toResponse);
        }
        List<Long> courseIds = courseRepository.findByInstructorId(instructorId, PageRequest.of(0, 500))
                .stream().map(Course::getId).toList();
        if (courseIds.isEmpty()) {
            return Page.empty(pageable);
        }
        return questionRepository.findByCourseIdInOrderByCreatedAtDesc(courseIds, pageable).map(this::toResponse);
    }

    @Transactional
    public QuestionResponse answer(Long questionId, Long instructorId, boolean admin, QuestionAnswerRequest request) {
        CourseQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 질문입니다."));
        Course course = getCourse(question.getCourseId());
        if (!admin) {
            verifyOwner(course, instructorId);
        }
        question.answer(instructorId, request.answer().trim());
        notificationService.send(question.getUserId(), "질문 답변 등록", course.getTitle() + " 강의 질문에 답변이 등록되었습니다.");
        return toResponse(question);
    }

    private Course getCourse(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 강의입니다."));
    }

    private void verifyOwner(Course course, Long instructorId) {
        if (!course.getInstructorId().equals(instructorId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "FORBIDDEN", "본인 강의의 질문만 관리할 수 있습니다.");
        }
    }

    private QuestionResponse toResponse(CourseQuestion question) {
        String studentName = userRepository.findById(question.getUserId()).map(user -> user.getName()).orElse("탈퇴한 사용자");
        String courseTitle = courseRepository.findById(question.getCourseId()).map(Course::getTitle).orElse("삭제된 강의");
        return new QuestionResponse(question, studentName, courseTitle);
    }
}
