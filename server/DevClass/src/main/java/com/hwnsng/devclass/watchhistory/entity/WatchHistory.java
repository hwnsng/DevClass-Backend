package com.hwnsng.devclass.watchhistory.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "watch_history",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "lesson_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WatchHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "lesson_id", nullable = false)
    private Long lessonId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    /** 마지막 재생 위치 (초 단위) */
    @Column(name = "last_position_seconds", nullable = false)
    private int lastPositionSeconds;

    private LocalDateTime watchedAt;

    @PrePersist @PreUpdate
    protected void onUpdate() { watchedAt = LocalDateTime.now(); }

    public static WatchHistory create(Long userId, Long lessonId, Long courseId, int positionSeconds) {
        WatchHistory wh = new WatchHistory();
        wh.userId              = userId;
        wh.lessonId            = lessonId;
        wh.courseId            = courseId;
        wh.lastPositionSeconds = positionSeconds;
        return wh;
    }

    public void updatePosition(int positionSeconds) {
        this.lastPositionSeconds = positionSeconds;
    }
}
