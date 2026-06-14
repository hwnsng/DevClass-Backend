package com.hwnsng.devclass.bookmark.dto;

import com.hwnsng.devclass.bookmark.entity.Bookmark;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class BookmarkResponse {
    private final Long bookmarkId;
    private final Long userId;
    private final Long lessonId;
    private final int positionSeconds;
    private final String note;
    private final LocalDateTime createdAt;

    public BookmarkResponse(Bookmark bookmark) {
        this.bookmarkId      = bookmark.getId();
        this.userId          = bookmark.getUserId();
        this.lessonId        = bookmark.getLessonId();
        this.positionSeconds = bookmark.getPositionSeconds();
        this.note            = bookmark.getNote();
        this.createdAt       = bookmark.getCreatedAt();
    }
}
