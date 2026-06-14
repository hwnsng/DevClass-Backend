package com.hwnsng.devclass.review.dto;

import com.hwnsng.devclass.review.entity.Review;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ReviewResponse {
    private final Long reviewId;
    private final Long userId;
    private final Long courseId;
    private final int rating;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ReviewResponse(Review review) {
        this.reviewId  = review.getId();
        this.userId    = review.getUserId();
        this.courseId  = review.getCourseId();
        this.rating    = review.getRating();
        this.content   = review.getContent();
        this.createdAt = review.getCreatedAt();
        this.updatedAt = review.getUpdatedAt();
    }
}
