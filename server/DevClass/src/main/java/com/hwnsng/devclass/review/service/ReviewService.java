package com.hwnsng.devclass.review.service;

import com.hwnsng.devclass.common.exception.CustomException;
import com.hwnsng.devclass.course.entity.Course;
import com.hwnsng.devclass.course.repository.CourseRepository;
import com.hwnsng.devclass.progress.entity.Progress;
import com.hwnsng.devclass.progress.repository.ProgressRepository;
import com.hwnsng.devclass.review.dto.ReviewRequest;
import com.hwnsng.devclass.review.dto.ReviewResponse;
import com.hwnsng.devclass.review.entity.Review;
import com.hwnsng.devclass.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository   reviewRepository;
    private final ProgressRepository progressRepository;
    private final CourseRepository   courseRepository;

    public List<ReviewResponse> getCourseReviews(Long courseId) {
        return reviewRepository.findByCourseIdOrderByCreatedAtDesc(courseId)
                .stream().map(ReviewResponse::new).toList();
    }

    @Transactional
    public ReviewResponse createReview(Long courseId, ReviewRequest req) {
        // 80% 이상 수강 확인
        Progress progress = progressRepository
                .findByUserIdAndCourseId(req.getUserId(), courseId)
                .orElseThrow(() -> new CustomException(
                        HttpStatus.FORBIDDEN, "NOT_ENROLLED", "수강 중인 강의가 아닙니다."));

        if (progress.getPercent() < 80) {
            throw new CustomException(
                    HttpStatus.FORBIDDEN, "REVIEW_NOT_ALLOWED",
                    "강의의 80% 이상 수강 후 리뷰를 작성할 수 있습니다. (현재: " + progress.getPercent() + "%)");
        }

        if (reviewRepository.existsByUserIdAndCourseId(req.getUserId(), courseId)) {
            throw new CustomException(
                    HttpStatus.CONFLICT, "ALREADY_REVIEWED", "이미 리뷰를 작성한 강의입니다.");
        }

        Review review = reviewRepository.save(
                Review.create(req.getUserId(), courseId, req.getRating(), req.getContent()));

        // 강의 평균 평점 갱신
        updateCourseRating(courseId);

        return new ReviewResponse(review);
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest req) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND, "NOT_FOUND", "리뷰를 찾을 수 없습니다."));

        if (!review.getUserId().equals(req.getUserId())) {
            throw new CustomException(
                    HttpStatus.FORBIDDEN, "FORBIDDEN", "본인의 리뷰만 수정할 수 있습니다.");
        }

        review.update(req.getRating(), req.getContent());
        updateCourseRating(review.getCourseId());
        return new ReviewResponse(review);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND, "NOT_FOUND", "리뷰를 찾을 수 없습니다."));

        if (!review.getUserId().equals(userId)) {
            throw new CustomException(
                    HttpStatus.FORBIDDEN, "FORBIDDEN", "본인의 리뷰만 삭제할 수 있습니다.");
        }

        Long courseId = review.getCourseId();
        reviewRepository.delete(review);
        updateCourseRating(courseId);
    }

    private void updateCourseRating(Long courseId) {
        List<Review> reviews = reviewRepository.findByCourseIdOrderByCreatedAtDesc(courseId);
        if (reviews.isEmpty()) return;

        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course != null) course.updateRating(Math.round(avg * 10.0) / 10.0);
    }
}
