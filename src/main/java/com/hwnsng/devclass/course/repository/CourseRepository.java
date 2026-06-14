package com.hwnsng.devclass.course.repository;

import com.hwnsng.devclass.course.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findByTitleContainingOrDescriptionContaining(
            String title, String description, Pageable pageable);

    Page<Course> findByInstructorId(Long instructorId, Pageable pageable);

    Page<Course> findByInstructorIdAndTitleContaining(Long instructorId, String title, Pageable pageable);

    Page<Course> findByStatus(Course.CourseStatus status, Pageable pageable);

    Page<Course> findByStatusAndTitleContainingOrStatusAndDescriptionContaining(
            Course.CourseStatus titleStatus, String title,
            Course.CourseStatus descriptionStatus, String description,
            Pageable pageable);

    List<Course> findTop10ByOrderByCreatedAtDesc();
}
