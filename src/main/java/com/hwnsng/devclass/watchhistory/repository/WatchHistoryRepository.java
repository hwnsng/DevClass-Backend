package com.hwnsng.devclass.watchhistory.repository;

import com.hwnsng.devclass.watchhistory.entity.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
    Optional<WatchHistory> findByUserIdAndLessonId(Long userId, Long lessonId);
    List<WatchHistory> findByUserIdAndCourseIdOrderByWatchedAtDesc(Long userId, Long courseId);
}
