ALTER TABLE courses
    ADD COLUMN status ENUM('PENDING','PUBLISHED','HIDDEN') NOT NULL DEFAULT 'PUBLISHED' AFTER student_count;

CREATE INDEX idx_courses_status_created ON courses(status, created_at);

CREATE TABLE course_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(160) NOT NULL,
    content TEXT NOT NULL,
    status ENUM('OPEN','ANSWERED') NOT NULL DEFAULT 'OPEN',
    answer TEXT,
    answered_by BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    answered_at DATETIME,
    CONSTRAINT fk_question_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_question_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_question_answerer FOREIGN KEY (answered_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_questions_course_created (course_id, created_at),
    INDEX idx_questions_status_created (status, created_at)
);
