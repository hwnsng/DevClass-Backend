package com.hwnsng.devclass.report.repository;

import com.hwnsng.devclass.report.entity.CourseReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CourseReportRepository extends JpaRepository<CourseReport, Long> {
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
    List<CourseReport> findByCourseId(Long courseId);

    @Query("SELECT r.courseId, COUNT(r) FROM CourseReport r GROUP BY r.courseId")
    List<Object[]> countByCourse();
}
