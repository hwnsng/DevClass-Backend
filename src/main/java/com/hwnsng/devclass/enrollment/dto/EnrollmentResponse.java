package com.hwnsng.devclass.enrollment.dto;

import com.hwnsng.devclass.enrollment.entity.Enrollment;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class EnrollmentResponse {
    private final Long enrollmentId;
    private final Long userId;
    private final Long courseId;
    private final String status;
    private final LocalDate enrolledAt;

    public EnrollmentResponse(Enrollment e) {
        this.enrollmentId = e.getId();
        this.userId = e.getUserId();
        this.courseId = e.getCourseId();
        this.status = e.getStatus().name();
        this.enrolledAt = e.getEnrolledAt().toLocalDate();
    }
}