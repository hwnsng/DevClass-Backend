package com.hwnsng.devclass.watchhistory.service;

import com.hwnsng.devclass.watchhistory.dto.WatchRequest;
import com.hwnsng.devclass.watchhistory.dto.WatchResponse;
import com.hwnsng.devclass.watchhistory.entity.WatchHistory;
import com.hwnsng.devclass.watchhistory.repository.WatchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WatchHistoryService {

    private final WatchHistoryRepository watchHistoryRepository;

    /** 레슨별 수강 히스토리 저장/갱신 (Upsert) */
    @Transactional
    public WatchResponse savePosition(Long lessonId, WatchRequest req) {
        Optional<WatchHistory> existing =
                watchHistoryRepository.findByUserIdAndLessonId(req.getUserId(), lessonId);

        WatchHistory wh;
        if (existing.isPresent()) {
            wh = existing.get();
            wh.updatePosition(req.getPositionSeconds());
        } else {
            wh = WatchHistory.create(req.getUserId(), lessonId, req.getCourseId(), req.getPositionSeconds());
            watchHistoryRepository.save(wh);
        }

        return new WatchResponse(wh);
    }

    /** 특정 레슨의 이어보기 위치 조회 */
    public WatchResponse getLessonPosition(Long userId, Long lessonId) {
        return watchHistoryRepository.findByUserIdAndLessonId(userId, lessonId)
                .map(WatchResponse::new)
                .orElse(null);
    }

    /** 강의의 전체 수강 히스토리 조회 (최근 순) */
    public List<WatchResponse> getCourseHistory(Long userId, Long courseId) {
        return watchHistoryRepository
                .findByUserIdAndCourseIdOrderByWatchedAtDesc(userId, courseId)
                .stream().map(WatchResponse::new).toList();
    }
}
