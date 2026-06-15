package com.hwnsng.devclass.course.dto;

import com.hwnsng.devclass.course.entity.Course;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class CourseListResponse {
    private final Long courseId;
    private final String title;
    private final String instructorName;
    private final Integer price;
    private final Double ratingAvg;
    private final Integer studentCount;
    private final LocalDate createdAt;
    private final String thumbnailUrl;
    private final String status;

    public CourseListResponse(Course course, String instructorName) {
        this.courseId = course.getId();
        this.title = course.getTitle();
        this.instructorName = instructorName;
        this.price = course.getPrice();
        this.ratingAvg = course.getRatingAvg();
        this.studentCount = course.getStudentCount();
        this.createdAt = course.getCreatedAt().toLocalDate();
        this.thumbnailUrl = course.getThumbnailUrl() != null
                ? "/api/courses/" + course.getId() + "/thumbnail"
                : null;
        this.status = course.getStatus().name();
    }
}
