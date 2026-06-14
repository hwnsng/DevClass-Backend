package com.hwnsng.devclass.progress.controller;

import com.hwnsng.devclass.progress.dto.ProgressResponse;
import com.hwnsng.devclass.progress.dto.ProgressUpdateRequest;
import com.hwnsng.devclass.progress.service.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Progress", description = "학습 진도 API")
@RestController
@RequestMapping("/api/users/{userId}/courses/{courseId}/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @Operation(summary = "진도 조회", description = "특정 사용자의 특정 강의 진도를 조회합니다.")
    @GetMapping
    public ResponseEntity<ProgressResponse> getProgress(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "강의 ID") @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(progressService.getProgress(userId, courseId));
    }

    @Operation(summary = "진도 업데이트", description = "진도율과 마지막 레슨을 저장합니다. 수강하지 않은 강의면 403 반환")
    @PutMapping
    public ResponseEntity<ProgressResponse> updateProgress(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "강의 ID") @PathVariable Long courseId,
            @Valid @RequestBody ProgressUpdateRequest req
    ) {
        return ResponseEntity.ok(progressService.updateProgress(userId, courseId, req));
    }
}