package com.hwnsng.devclass.bookmark.service;

import com.hwnsng.devclass.bookmark.dto.BookmarkRequest;
import com.hwnsng.devclass.bookmark.dto.BookmarkResponse;
import com.hwnsng.devclass.bookmark.entity.Bookmark;
import com.hwnsng.devclass.bookmark.repository.BookmarkRepository;
import com.hwnsng.devclass.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    public List<BookmarkResponse> getLessonBookmarks(Long userId, Long lessonId) {
        return bookmarkRepository
                .findByUserIdAndLessonIdOrderByPositionSecondsAsc(userId, lessonId)
                .stream().map(BookmarkResponse::new).toList();
    }

    @Transactional
    public BookmarkResponse addBookmark(Long lessonId, BookmarkRequest req) {
        Bookmark bookmark = bookmarkRepository.save(
                Bookmark.create(req.getUserId(), lessonId, req.getPositionSeconds(), req.getNote()));
        return new BookmarkResponse(bookmark);
    }

    @Transactional
    public void deleteBookmark(Long bookmarkId, Long userId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND, "NOT_FOUND", "북마크를 찾을 수 없습니다."));

        if (!bookmark.getUserId().equals(userId)) {
            throw new CustomException(
                    HttpStatus.FORBIDDEN, "FORBIDDEN", "본인의 북마크만 삭제할 수 있습니다.");
        }

        bookmarkRepository.delete(bookmark);
    }
}
