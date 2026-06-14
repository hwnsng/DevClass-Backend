package com.hwnsng.devclass.progress.service;

import com.hwnsng.devclass.common.exception.CustomException;
import com.hwnsng.devclass.enrollment.entity.Enrollment;
import com.hwnsng.devclass.enrollment.repository.EnrollmentRepository;
import com.hwnsng.devclass.progress.dto.ProgressResponse;
import com.hwnsng.devclass.progress.dto.ProgressUpdateRequest;
import com.hwnsng.devclass.progress.entity.Progress;
import com.hwnsng.devclass.progress.repository.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final EnrollmentRepository enrollmentRepository;

    public ProgressResponse getProgress(Long userId, Long courseId) {
        Progress progress = progressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "NOT_FOUND", "진도 정보가 없습니다."));
        return new ProgressResponse(progress);
    }

    @Transactional
    public ProgressResponse updateProgress(Long userId, Long courseId, ProgressUpdateRequest req) {
        // 수강 여부 확인
        boolean enrolled = enrollmentRepository.existsByUserIdAndCourseIdAndStatus(
                userId, courseId, Enrollment.EnrollmentStatus.ENROLLED);
        if (!enrolled) {
            throw new CustomException(HttpStatus.FORBIDDEN, "NOT_ENROLLED", "수강 중인 강의가 아닙니다.");
        }

        // 있으면 업데이트, 없으면 생성 (upsert)
        Progress progress = progressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseGet(() -> Progress.create(userId, courseId));

        progress.update(req.getPercent(), req.getLastLessonId());
        return new ProgressResponse(progressRepository.save(progress));
    }
}