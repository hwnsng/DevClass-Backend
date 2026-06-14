-- ==============================================
-- P3: 성능 인덱스 (V1에 없는 새 인덱스만 추가)
-- ==============================================
-- V1에 이미 있는 것: idx_courses_created_at, idx_courses_instructor,
--                    idx_enrollments_user, idx_progress_user_course
CREATE INDEX idx_enrollments_course ON enrollments(course_id);
CREATE INDEX idx_reports_course_id  ON course_reports(course_id);
CREATE INDEX idx_notifications_user ON notifications(user_id);

-- ==============================================
-- P3: 구독 (강사 팔로우 → 신규 강의 알림)
-- ==============================================
CREATE TABLE IF NOT EXISTS subscriptions (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    instructor_id BIGINT NOT NULL,
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sub_user       FOREIGN KEY (user_id)       REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_sub_instructor FOREIGN KEY (instructor_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_subscription (user_id, instructor_id)
);

-- ==============================================
-- P3: 스케줄러 실행 이력
-- ==============================================
CREATE TABLE IF NOT EXISTS job_runs (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_name    VARCHAR(100) NOT NULL,
    status      ENUM('RUNNING','SUCCESS','FAIL') NOT NULL DEFAULT 'RUNNING',
    message     TEXT,
    started_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    finished_at DATETIME,
    INDEX idx_job_runs_name (job_name)
);

-- ==============================================
-- P3: 인기 강의 스냅샷 (주간 스케줄러가 갱신)
-- ==============================================
CREATE TABLE IF NOT EXISTS popular_course_snapshots (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id     BIGINT NOT NULL,
    student_count INT    NOT NULL,
    rank_order    INT    NOT NULL,
    snapshot_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_snapshot_at (snapshot_at)
);
