package com.hwnsng.devclass.course.repository;

import com.hwnsng.devclass.course.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findByTitleContainingOrDescriptionContaining(
            String title, String description, Pageable pageable);

    Page<Course> findByInstructorId(Long instructorId, Pageable pageable);

    Page<Course> findByInstructorIdAndTitleContaining(Long instructorId, String title, Pageable pageable);
}