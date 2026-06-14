package com.hwnsng.devclass.question.repository;

import com.hwnsng.devclass.question.entity.CourseQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface CourseQuestionRepository extends JpaRepository<CourseQuestion, Long> {
    Page<CourseQuestion> findByCourseIdOrderByCreatedAtDesc(Long courseId, Pageable pageable);
    Page<CourseQuestion> findByCourseIdInOrderByCreatedAtDesc(Collection<Long> courseIds, Pageable pageable);
}
