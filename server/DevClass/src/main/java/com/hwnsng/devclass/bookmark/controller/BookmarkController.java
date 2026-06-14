package com.hwnsng.devclass.bookmark.controller;

import com.hwnsng.devclass.bookmark.dto.BookmarkRequest;
import com.hwnsng.devclass.bookmark.dto.BookmarkResponse;
import com.hwnsng.devclass.bookmark.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Bookmark", description = "북마크 API")
@RestController
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "레슨 북마크 목록 조회")
    @GetMapping("/api/lessons/{lessonId}/bookmarks")
    public ResponseEntity<List<BookmarkResponse>> getLessonBookmarks(
            @PathVariable Long lessonId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(bookmarkService.getLessonBookmarks(userId, lessonId));
    }

    @Operation(summary = "북마크 추가")
    @PostMapping("/api/lessons/{lessonId}/bookmarks")
    public ResponseEntity<BookmarkResponse> addBookmark(
            @PathVariable Long lessonId,
            @RequestBody BookmarkRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookmarkService.addBookmark(lessonId, req));
    }

    @Operation(summary = "북마크 삭제")
    @DeleteMapping("/api/bookmarks/{bookmarkId}")
    public ResponseEntity<Void> deleteBookmark(
            @PathVariable Long bookmarkId,
            @RequestParam Long userId) {
        bookmarkService.deleteBookmark(bookmarkId, userId);
        return ResponseEntity.noContent().build();
    }
}
