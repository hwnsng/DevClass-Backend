package com.hwnsng.devclass.enrollment.repository;

import com.hwnsng.devclass.enrollment.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);

    // 내 수강 목록 (취소 안 된 것만)
    Page<Enrollment> findByUserIdAndStatus(Long userId, Enrollment.EnrollmentStatus status, Pageable pageable);

    boolean existsByUserIdAndCourseIdAndStatus(Long userId, Long courseId, Enrollment.EnrollmentStatus status);

    @Query(value =
        "SELECT u.id as userId, u.name as name, DATE(e.enrolled_at) as enrolledAt, " +
        "COALESCE(p.percent, 0) as progressPercent " +
        "FROM enrollments e " +
        "JOIN users u ON e.user_id = u.id " +
        "LEFT JOIN progress p ON p.user_id = u.id AND p.course_id = e.course_id " +
        "WHERE e.course_id = :courseId AND e.status = 'ENROLLED' " +
        "ORDER BY e.enrolled_at DESC",
        nativeQuery = true)
    List<CourseStudentView> findStudentsByCourseId(@Param("courseId") Long courseId);
}