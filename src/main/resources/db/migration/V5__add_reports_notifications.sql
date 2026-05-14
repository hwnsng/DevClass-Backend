-- ==============================================
-- 강의 신고
-- ==============================================
CREATE TABLE course_reports (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    course_id   BIGINT       NOT NULL,
    reason      VARCHAR(100) NOT NULL,
    description TEXT,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_report_user   FOREIGN KEY (user_id)   REFERENCES users(id),
    CONSTRAINT fk_report_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    UNIQUE KEY unique_report (user_id, course_id),
    INDEX idx_reports_course (course_id)
);

-- ==============================================
-- 알림
-- ==============================================
CREATE TABLE notifications (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT        NOT NULL,
    title      VARCHAR(255)  NOT NULL,
    message    TEXT          NOT NULL,
    is_read    BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_noti_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_noti_user (user_id)
);
