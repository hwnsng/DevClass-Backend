package com.hwnsng.devclass.course.dto;

import com.hwnsng.devclass.course.entity.Course;
import com.hwnsng.devclass.lesson.entity.Lesson;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

@Getter
public class CourseDetailResponse {
    private final Long courseId;
    private final String title;
    private final String description;
    private final Long instructorId;
    private final String instructorName;
    private final Integer price;
    private final Double ratingAvg;
    private final Integer studentCount;
    private final LocalDate createdAt;
    private final String thumbnailUrl;
    private final String status;
    private final List<LessonSummary> lessons;

    public CourseDetailResponse(Course course, String instructorName) {
        this.courseId = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.instructorId = course.getInstructorId();
        this.instructorName = instructorName;
        this.price = course.getPrice();
        this.ratingAvg = course.getRatingAvg();
        this.studentCount = course.getStudentCount();
        this.createdAt = course.getCreatedAt().toLocalDate();
        this.thumbnailUrl = course.getThumbnailUrl() != null
                ? "/api/courses/" + course.getId() + "/thumbnail"
                : null;
        this.status = course.getStatus().name();
        this.lessons = course.getLessons().stream().map(LessonSummary::new).toList();
    }

    @Getter
    public static class LessonSummary {
        private final Long lessonId;
        private final String title;
        private final String description;
        private final Integer order;
        private final String videoUrl;

        public LessonSummary(Lesson lesson) {
            this.lessonId = lesson.getId();
            this.title = lesson.getTitle();
            this.description = lesson.getDescription();
            this.order = lesson.getLessonOrder();
            this.videoUrl = lesson.getVideoUrl();
        }
    }
}
