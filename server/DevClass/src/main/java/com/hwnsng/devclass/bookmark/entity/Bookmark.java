package com.hwnsng.devclass.bookmark.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookmarks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "lesson_id", nullable = false)
    private Long lessonId;

    @Column(name = "position_seconds", nullable = false)
    private int positionSeconds;

    @Column(length = 500)
    private String note;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public static Bookmark create(Long userId, Long lessonId, int positionSeconds, String note) {
        Bookmark b = new Bookmark();
        b.userId          = userId;
        b.lessonId        = lessonId;
        b.positionSeconds = positionSeconds;
        b.note            = note;
        return b;
    }
}
