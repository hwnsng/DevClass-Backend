package com.hwnsng.devclass.question.dto;

import com.hwnsng.devclass.question.entity.CourseQuestion;

import java.time.LocalDateTime;

public record QuestionResponse(
        Long questionId,
        Long courseId,
        Long userId,
        String studentName,
        String courseTitle,
        String title,
        String content,
        String status,
        String answer,
        Long answeredBy,
        LocalDateTime createdAt,
        LocalDateTime answeredAt
) {
    public QuestionResponse(CourseQuestion question, String studentName, String courseTitle) {
        this(question.getId(), question.getCourseId(), question.getUserId(), studentName, courseTitle,
                question.getTitle(), question.getContent(), question.getStatus().name(), question.getAnswer(),
                question.getAnsweredBy(), question.getCreatedAt(), question.getAnsweredAt());
    }
}
