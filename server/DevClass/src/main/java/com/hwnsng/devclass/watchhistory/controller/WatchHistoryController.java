package com.hwnsng.devclass.watchhistory.controller;

import com.hwnsng.devclass.watchhistory.dto.WatchRequest;
import com.hwnsng.devclass.watchhistory.dto.WatchResponse;
import com.hwnsng.devclass.watchhistory.service.WatchHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "WatchHistory", description = "수강 히스토리 API")
@RestController
@RequiredArgsConstructor
public class WatchHistoryController {

    private final WatchHistoryService watchHistoryService;

    @Operation(summary = "수강 위치 저장 (이어보기)", description = "레슨의 현재 재생 위치를 저장합니다.")
    @PostMapping("/api/lessons/{lessonId}/watch")
    public ResponseEntity<WatchResponse> savePosition(
            @PathVariable Long lessonId,
            @RequestBody WatchRequest req) {
        return ResponseEntity.ok(watchHistoryService.savePosition(lessonId, req));
    }

    @Operation(summary = "레슨 이어보기 위치 조회")
    @GetMapping("/api/lessons/{lessonId}/watch")
    public ResponseEntity<WatchResponse> getLessonPosition(
            @PathVariable Long lessonId,
            @RequestParam Long userId) {
        WatchResponse response = watchHistoryService.getLessonPosition(userId, lessonId);
        return response != null
                ? ResponseEntity.ok(response)
                : ResponseEntity.noContent().build();
    }

    @Operation(summary = "강의 전체 수강 히스토리 조회")
    @GetMapping("/api/users/{userId}/courses/{courseId}/watch-history")
    public ResponseEntity<List<WatchResponse>> getCourseHistory(
            @PathVariable Long userId,
            @PathVariable Long courseId) {
        return ResponseEntity.ok(watchHistoryService.getCourseHistory(userId, courseId));
    }
}
