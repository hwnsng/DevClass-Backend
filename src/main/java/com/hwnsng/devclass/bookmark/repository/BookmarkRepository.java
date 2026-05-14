package com.hwnsng.devclass.bookmark.repository;

import com.hwnsng.devclass.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUserIdAndLessonIdOrderByPositionSecondsAsc(Long userId, Long lessonId);
    List<Bookmark> findByUserIdOrderByCreatedAtDesc(Long userId);
}
