package com.hwnsng.devclass.scheduler;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "popular_course_snapshots")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PopularCourseSnapshot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "student_count", nullable = false)
    private int studentCount;

    @Column(name = "rank_order", nullable = false)
    private int rankOrder;

    @Column(name = "snapshot_at")
    private LocalDateTime snapshotAt;

    @PrePersist
    protected void onCreate() { snapshotAt = LocalDateTime.now(); }

    public static PopularCourseSnapshot of(Long courseId, int studentCount, int rank) {
        PopularCourseSnapshot s = new PopularCourseSnapshot();
        s.courseId = courseId;
        s.studentCount = studentCount;
        s.rankOrder = rank;
        s.snapshotAt = LocalDateTime.now();
        return s;
    }
}
