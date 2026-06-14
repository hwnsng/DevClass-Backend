package com.hwnsng.devclass.question.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionStatus status = QuestionStatus.OPEN;

    @Column(columnDefinition = "TEXT")
    private String answer;

    private Long answeredBy;
    private LocalDateTime createdAt;
    private LocalDateTime answeredAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public static CourseQuestion create(Long courseId, Long userId, String title, String content) {
        CourseQuestion question = new CourseQuestion();
        question.courseId = courseId;
        question.userId = userId;
        question.title = title;
        question.content = content;
        return question;
    }

    public void answer(Long instructorId, String answer) {
        this.answer = answer;
        this.answeredBy = instructorId;
        this.answeredAt = LocalDateTime.now();
        this.status = QuestionStatus.ANSWERED;
    }

    public enum QuestionStatus {
        OPEN, ANSWERED
    }
}
