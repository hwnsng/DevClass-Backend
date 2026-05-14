package com.hwnsng.devclass.course.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.hwnsng.devclass.lesson.entity.Lesson;

@Entity
@Table(name = "courses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long instructorId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer price = 0;

    @Column(name = "rating_avg", columnDefinition = "DECIMAL(2,1)")
    private Double ratingAvg = 0.0;

    @Column(name = "student_count")
    private Integer studentCount = 0;

    private String thumbnailUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("lessonOrder ASC")
    private List<Lesson> lessons = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static Course create(Long instructorId, String title, String description, Integer price) {
        Course c = new Course();
        c.instructorId = instructorId;
        c.title = title;
        c.description = description;
        c.price = price;
        return c;
    }

    public void update(String title, String description, Integer price) {
        this.title = title;
        this.description = description;
        this.price = price;
    }

    public void updateThumbnail(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    // 수강 등록 시 호출
    public void increaseStudentCount() {
        this.studentCount++;
    }

    // 수강 취소 시 호출
    public void decreaseStudentCount() {
        if (this.studentCount > 0) {
            this.studentCount--;
        }
    }

    // 리뷰 평균 평점 갱신
    public void updateRating(double ratingAvg) {
        this.ratingAvg = ratingAvg;
    }
}