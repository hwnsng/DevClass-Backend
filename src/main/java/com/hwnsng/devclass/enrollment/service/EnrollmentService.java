package com.hwnsng.devclass.enrollment.service;

import com.hwnsng.devclass.common.exception.CustomException;
import com.hwnsng.devclass.course.entity.Course;
import com.hwnsng.devclass.course.repository.CourseRepository;
import com.hwnsng.devclass.enrollment.dto.EnrollmentRequest;
import com.hwnsng.devclass.enrollment.dto.EnrollmentResponse;
import com.hwnsng.devclass.enrollment.entity.Enrollment;
import com.hwnsng.devclass.enrollment.repository.CourseStudentView;
import com.hwnsng.devclass.enrollment.repository.EnrollmentRepository;
import com.hwnsng.devclass.progress.entity.Progress;
import com.hwnsng.devclass.progress.repository.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final ProgressRepository progressRepository;

    @Transactional
    public EnrollmentResponse enroll(EnrollmentRequest req) {
        // 강의 존재 확인 + course 객체 보관
        Course course = courseRepository.findById(req.getCourseId())
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 강의입니다."));

        // 기존 수강 레코드 확인 (CANCELLED 포함)
        Optional<Enrollment> existing = enrollmentRepository
                .findByUserIdAndCourseId(req.getUserId(), req.getCourseId());

        Enrollment enrollment;
        if (existing.isPresent()) {
            enrollment = existing.get();

            // 이미 수강 중이면 409
            if (enrollment.getStatus() == Enrollment.EnrollmentStatus.ENROLLED) {
                throw new CustomException(
                        HttpStatus.CONFLICT, "ALREADY_ENROLLED", "이미 수강 중인 강의입니다.");
            }

            // 취소 상태면 재등록 + student_count 증가
            enrollment.reenroll();
            enrollmentRepository.save(enrollment);
            course.increaseStudentCount();  // 재등록 시 증가
        } else {
            // 처음 등록 + student_count 증가
            enrollment = Enrollment.create(req.getUserId(), req.getCourseId());
            enrollmentRepository.save(enrollment);
            course.increaseStudentCount();  // 신규 등록 시 증가
        }

        // 진도율 초기화 (신규/재등록 모두 0%로 리셋)
        progressRepository.findByUserIdAndCourseId(req.getUserId(), req.getCourseId())
                .ifPresent(progress -> {
                    progress.reset();
                    progressRepository.save(progress);
                });

        return new EnrollmentResponse(enrollment);
    }

    public Page<EnrollmentResponse> getMyEnrollments(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by(Sort.Direction.DESC, "enrolledAt"));
        return enrollmentRepository
                .findByUserIdAndStatus(userId, Enrollment.EnrollmentStatus.ENROLLED, pageable)
                .map(EnrollmentResponse::new);
    }

    public List<CourseStudentView> getCourseStudents(Long courseId) {
        return enrollmentRepository.findStudentsByCourseId(courseId);
    }

    @Transactional
    public void cancel(Long enrollmentId, Long userId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND, "NOT_FOUND", "수강 정보를 찾을 수 없습니다."));

        if (!enrollment.getUserId().equals(userId)) {
            throw new CustomException(
                    HttpStatus.FORBIDDEN, "FORBIDDEN", "본인의 수강만 취소할 수 있습니다.");
        }

        // 취소 처리 + student_count 감소
        Course course = courseRepository.findById(enrollment.getCourseId())
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 강의입니다."));

        enrollment.cancel();
        course.decreaseStudentCount();  // 취소 시 감소
    }
}