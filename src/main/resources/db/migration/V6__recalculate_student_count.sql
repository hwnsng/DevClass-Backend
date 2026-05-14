-- courses.student_count를 enrollments 테이블 기준으로 재계산
UPDATE courses c
SET student_count = (
    SELECT COUNT(*)
    FROM enrollments e
    WHERE e.course_id = c.id
      AND e.status = 'ENROLLED'
);
