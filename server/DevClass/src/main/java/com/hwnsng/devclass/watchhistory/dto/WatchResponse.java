package com.hwnsng.devclass.watchhistory.dto;

import com.hwnsng.devclass.watchhistory.entity.WatchHistory;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class WatchResponse {
    private final Long historyId;
    private final Long userId;
    private final Long lessonId;
    private final Long courseId;
    private final int lastPositionSeconds;
    private final LocalDateTime watchedAt;

    public WatchResponse(WatchHistory wh) {
        this.historyId           = wh.getId();
        this.userId              = wh.getUserId();
        this.lessonId            = wh.getLessonId();
        this.courseId            = wh.getCourseId();
        this.lastPositionSeconds = wh.getLastPositionSeconds();
        this.watchedAt           = wh.getWatchedAt();
    }
}
