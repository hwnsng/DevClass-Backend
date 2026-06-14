package com.hwnsng.devclass.course.service;

import com.hwnsng.devclass.common.exception.CustomException;
import com.hwnsng.devclass.course.dto.*;
import com.hwnsng.devclass.course.entity.Course;
import com.hwnsng.devclass.course.repository.CourseRepository;
import com.hwnsng.devclass.lesson.service.FileStorageService;
import com.hwnsng.devclass.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final FileStorageService fileStorageService;
    @Lazy
    private final SubscriptionService subscriptionService;

    public Page<CourseListResponse> getCourses(String query, int page, int size, String sort, Long instructorId) {
        Sort sorting = switch (sort) {
            case "popular" -> Sort.by(Sort.Direction.DESC, "ratingAvg").and(Sort.by(Sort.Direction.DESC, "studentCount"));
            case "rating"  -> Sort.by(Sort.Direction.DESC, "ratingAvg");
            default        -> Sort.by(Sort.Direction.DESC, "createdAt");
        };

        Pageable pageable = PageRequest.of(page - 1, size, sorting);

        Page<Course> courses;
        if (instructorId != null) {
            if (query != null && !query.isBlank()) {
                courses = courseRepository.findByInstructorIdAndTitleContaining(instructorId, query, pageable);
            } else {
                courses = courseRepository.findByInstructorId(instructorId, pageable);
            }
        } else if (query == null || query.isBlank()) {
            courses = courseRepository.findByStatus(Course.CourseStatus.PUBLISHED, pageable);
        } else {
            courses = courseRepository.findByStatusAndTitleContainingOrStatusAndDescriptionContaining(
                    Course.CourseStatus.PUBLISHED, query,
                    Course.CourseStatus.PUBLISHED, query,
                    pageable);
        }

        return courses.map(CourseListResponse::new);
    }

    public CourseDetailResponse getCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 강의입니다."));
        return new CourseDetailResponse(course);
    }

    @Transactional
    public CourseDetailResponse createCourse(CreateCourseRequest request) {
        Course course = Course.create(
                request.getInstructorId(),
                request.getTitle(),
                request.getDescription(),
                request.getPrice() != null ? request.getPrice() : 0
        );
        courseRepository.save(course);
        log.info("Course created: id={} title='{}' instructorId={}", course.getId(), course.getTitle(), course.getInstructorId());

        // 구독자 알림 (비동기)
        subscriptionService.notifyNewCourse(request.getInstructorId(), course.getId(), course.getTitle());

        return new CourseDetailResponse(course);
    }

    @Transactional
    public CourseDetailResponse updateCourse(Long courseId, UpdateCourseRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 강의입니다."));
        course.update(request.getTitle(), request.getDescription(), request.getPrice());
        return new CourseDetailResponse(course);
    }

    @Transactional
    public CourseDetailResponse uploadThumbnail(Long courseId, MultipartFile file) {
        validateImage(file);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 강의입니다."));

        if (course.getThumbnailUrl() != null) {
            fileStorageService.delete(course.getThumbnailUrl());
        }

        String relativePath = fileStorageService.storeThumbnail(file, courseId);
        course.updateThumbnail(relativePath);
        return new CourseDetailResponse(course);
    }

    public Resource getThumbnailResource(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 강의입니다."));

        if (course.getThumbnailUrl() == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "NO_THUMBNAIL", "썸네일이 없습니다.");
        }

        Resource resource = fileStorageService.getResource(course.getThumbnailUrl());
        if (!resource.exists()) {
            throw new CustomException(HttpStatus.NOT_FOUND, "FILE_NOT_FOUND", "썸네일 파일을 찾을 수 없습니다.");
        }
        return resource;
    }

    @Transactional
    public void deleteCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new CustomException(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 강의입니다.");
        }
        courseRepository.deleteById(courseId);
    }

    @Transactional
    public void updateStatus(Long courseId, Course.CourseStatus status) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 강의입니다."));
        if (status == Course.CourseStatus.PUBLISHED) {
            course.publish();
        } else if (status == Course.CourseStatus.HIDDEN) {
            course.hide();
        } else {
            throw new CustomException(HttpStatus.BAD_REQUEST, "INVALID_STATUS", "승인 또는 숨김 상태만 지정할 수 있습니다.");
        }
    }

    @Transactional
    public void deleteCourseByInstructor(Long courseId, Long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 강의입니다."));
        if (!course.getInstructorId().equals(instructorId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "FORBIDDEN", "본인의 강의만 삭제할 수 있습니다.");
        }
        if (course.getThumbnailUrl() != null) {
            fileStorageService.delete(course.getThumbnailUrl());
        }
        courseRepository.deleteById(courseId);
    }

    private void validateImage(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "INVALID_FORMAT", "파일명이 없습니다.");
        }
        String lower = name.toLowerCase();
        if (!lower.endsWith(".jpg") && !lower.endsWith(".jpeg") && !lower.endsWith(".png") && !lower.endsWith(".webp")) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "INVALID_FORMAT", "이미지 파일(jpg, png, webp)만 업로드 가능합니다.");
        }
        if (file.getSize() > 10L * 1024 * 1024) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "FILE_TOO_LARGE", "썸네일 크기는 10MB 이하여야 합니다.");
        }
    }
}
