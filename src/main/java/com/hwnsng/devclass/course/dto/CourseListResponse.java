package com.hwnsng.devclass.course.dto;

import com.hwnsng.devclass.course.entity.Course;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class CourseListResponse {
    private final Long courseId;
    private final String title;
    private final Integer price;
    private final Double ratingAvg;
    private final Integer studentCount;
    private final LocalDate createdAt;
    private final String thumbnailUrl;

    public CourseListResponse(Course course) {
        this.courseId = course.getId();
        this.title = course.getTitle();
        this.price = course.getPrice();
        this.ratingAvg = course.getRatingAvg();
        this.studentCount = course.getStudentCount();
        this.createdAt = course.getCreatedAt().toLocalDate();
        this.thumbnailUrl = course.getThumbnailUrl() != null
                ? "http://localhost:8080/uploads/" + course.getThumbnailUrl()
                : null;
    }
}
