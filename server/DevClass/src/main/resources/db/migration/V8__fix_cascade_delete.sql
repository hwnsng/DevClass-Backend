-- ==============================================
-- 강의 삭제 시 FK 제약 위반 수정
-- course_id / lesson_id FK에 ON DELETE CASCADE / SET NULL 추가
-- ==============================================

-- enrollments (기존 enrollments_ibfk_2 → CASCADE)
ALTER TABLE enrollments
    DROP FOREIGN KEY enrollments_ibfk_2,
    ADD CONSTRAINT fk_enrollment_course
        FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE;

-- progress.course_id → CASCADE
ALTER TABLE progress
    DROP FOREIGN KEY progress_ibfk_2,
    ADD CONSTRAINT fk_progress_course
        FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE;

-- progress.last_lesson_id → SET NULL (레슨 삭제 시 null 처리)
ALTER TABLE progress
    DROP FOREIGN KEY progress_ibfk_3,
    ADD CONSTRAINT fk_progress_last_lesson
        FOREIGN KEY (last_lesson_id) REFERENCES lessons(id) ON DELETE SET NULL;

-- cart_items
ALTER TABLE cart_items
    DROP FOREIGN KEY fk_cart_course,
    ADD CONSTRAINT fk_cart_course_v2
        FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE;

-- payment_items
ALTER TABLE payment_items
    DROP FOREIGN KEY fk_pi_course,
    ADD CONSTRAINT fk_pi_course_v2
        FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE;

-- reviews
ALTER TABLE reviews
    DROP FOREIGN KEY fk_review_course,
    ADD CONSTRAINT fk_review_course_v2
        FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE;

-- watch_history
ALTER TABLE watch_history
    DROP FOREIGN KEY fk_wh_course,
    ADD CONSTRAINT fk_wh_course_v2
        FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE;
