package com.hwnsng.devclass.enrollment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    private LocalDateTime enrolledAt;

    @PrePersist
    protected void onCreate() {
        enrolledAt = LocalDateTime.now();
    }

    public static Enrollment create(Long userId, Long courseId) {
        Enrollment e = new Enrollment();
        e.userId = userId;
        e.courseId = courseId;
        return e;
    }

    public void cancel() {
        this.status = EnrollmentStatus.CANCELLED;
    }
    public void reenroll() {
        this.status = EnrollmentStatus.ENROLLED;
        this.enrolledAt = LocalDateTime.now();
    }

    public enum EnrollmentStatus {
        ENROLLED, CANCELLED
    }
}