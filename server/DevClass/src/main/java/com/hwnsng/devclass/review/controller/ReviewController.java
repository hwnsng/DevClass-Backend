package com.hwnsng.devclass.review.controller;

import com.hwnsng.devclass.review.dto.ReviewRequest;
import com.hwnsng.devclass.review.dto.ReviewResponse;
import com.hwnsng.devclass.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Review", description = "리뷰 API")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "강의 리뷰 목록 조회")
    @GetMapping("/api/courses/{courseId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Long courseId) {
        return ResponseEntity.ok(reviewService.getCourseReviews(courseId));
    }

    @Operation(summary = "리뷰 작성 (80% 이상 수강 필요)")
    @PostMapping("/api/courses/{courseId}/reviews")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long courseId,
            @RequestBody ReviewRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(courseId, req));
    }

    @Operation(summary = "리뷰 수정")
    @PutMapping("/api/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewRequest req) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, req));
    }

    @Operation(summary = "리뷰 삭제")
    @DeleteMapping("/api/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }
}
