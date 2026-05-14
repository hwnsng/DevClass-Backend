package com.hwnsng.devclass.report.dto;

import com.hwnsng.devclass.report.entity.CourseReport;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ReportResponse {
    private Long id;
    private Long userId;
    private Long courseId;
    private String reason;
    private String description;
    private LocalDateTime createdAt;

    public ReportResponse(CourseReport r) {
        this.id = r.getId();
        this.userId = r.getUserId();
        this.courseId = r.getCourseId();
        this.reason = r.getReason();
        this.description = r.getDescription();
        this.createdAt = r.getCreatedAt();
    }
}
