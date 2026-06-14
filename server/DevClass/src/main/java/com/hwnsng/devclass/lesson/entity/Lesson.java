package com.hwnsng.devclass.lesson.entity;

import com.hwnsng.devclass.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lessons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lesson {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String videoUrl;

    private String originalFileName;

    private Long fileSize;

    @Column(name = "lesson_order", nullable = false)
    private Integer lessonOrder;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public static Lesson create(Course course, String title, String description, Integer lessonOrder) {
        Lesson lesson = new Lesson();
        lesson.course = course;
        lesson.title = title;
        lesson.description = description;
        lesson.lessonOrder = lessonOrder;
        return lesson;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateVideo(String videoUrl, String originalFileName, Long fileSize) {
        this.videoUrl = videoUrl;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
    }
}
