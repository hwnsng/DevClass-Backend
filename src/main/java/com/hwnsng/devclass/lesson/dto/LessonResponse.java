package com.hwnsng.devclass.lesson.dto;

import com.hwnsng.devclass.lesson.entity.Lesson;
import lombok.Getter;

@Getter
public class LessonResponse {
    private final Long lessonId;
    private final Long courseId;
    private final String title;
    private final String description;
    private final Integer order;
    private final String originalFileName;
    private final Long fileSize;
    private final boolean hasVideo;

    public LessonResponse(Lesson lesson) {
        this.lessonId = lesson.getId();
        this.courseId = lesson.getCourse().getId();
        this.title = lesson.getTitle();
        this.description = lesson.getDescription();
        this.order = lesson.getLessonOrder();
        this.originalFileName = lesson.getOriginalFileName();
        this.fileSize = lesson.getFileSize();
        this.hasVideo = lesson.getVideoUrl() != null;
    }
}
