package com.hwnsng.devclass.progress.dto;

import com.hwnsng.devclass.progress.entity.Progress;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ProgressResponse {
    private final Long courseId;
    private final Long userId;
    private final Integer percent;
    private final Long lastLessonId;
    private final LocalDateTime updatedAt;

    public ProgressResponse(Progress p) {
        this.courseId = p.getCourseId();
        this.userId = p.getUserId();
        this.percent = p.getPercent();
        this.lastLessonId = p.getLastLessonId();
        this.updatedAt = p.getUpdatedAt();
    }
}