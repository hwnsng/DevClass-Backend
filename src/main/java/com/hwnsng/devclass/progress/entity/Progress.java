package com.hwnsng.devclass.progress.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Progress {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "last_lesson_id")
    private Long lastLessonId;

    private Integer percent = 0;

    private LocalDateTime updatedAt;

    @PrePersist @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static Progress create(Long userId, Long courseId) {
        Progress p = new Progress();
        p.userId = userId;
        p.courseId = courseId;
        p.percent = 0;
        return p;
    }

    public void update(Integer percent, Long lastLessonId) {
        this.percent = Math.max(this.percent, percent);
        this.lastLessonId = lastLessonId;
    }

    public void reset() {
        this.percent = 0;
        this.lastLessonId = null;
    }
}
