-- ==============================================
-- 장바구니
-- ==============================================
CREATE TABLE cart_items (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT   NOT NULL,
    course_id  BIGINT   NOT NULL,
    added_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_user   FOREIGN KEY (user_id)   REFERENCES users(id),
    CONSTRAINT fk_cart_course FOREIGN KEY (course_id) REFERENCES courses(id),
    UNIQUE KEY unique_cart_item (user_id, course_id),
    INDEX idx_cart_user (user_id)
);

-- ==============================================
-- 결제
-- ==============================================
CREATE TABLE payments (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT       NOT NULL,
    total_amount     INT          NOT NULL,
    status           ENUM('PENDING','PAID','CANCELLED','PARTIAL_CANCELLED') NOT NULL DEFAULT 'PENDING',
    toss_payment_key VARCHAR(200),
    toss_order_id    VARCHAR(200) NOT NULL,
    paid_at          DATETIME,
    created_at       DATETIME     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_payments_user  (user_id),
    INDEX idx_payments_order (toss_order_id)
);

CREATE TABLE payment_items (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id    BIGINT NOT NULL,
    course_id     BIGINT NOT NULL,
    amount        INT    NOT NULL,
    status        ENUM('ACTIVE','CANCELLED','REFUNDED') NOT NULL DEFAULT 'ACTIVE',
    cancel_reason VARCHAR(500),
    cancelled_at  DATETIME,
    CONSTRAINT fk_pi_payment FOREIGN KEY (payment_id) REFERENCES payments(id),
    CONSTRAINT fk_pi_course  FOREIGN KEY (course_id)  REFERENCES courses(id),
    INDEX idx_pi_payment (payment_id)
);

-- ==============================================
-- 환불
-- ==============================================
CREATE TABLE refunds (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id      BIGINT       NOT NULL,
    payment_item_id BIGINT,
    user_id         BIGINT       NOT NULL,
    amount          INT          NOT NULL,
    reason          VARCHAR(500),
    refund_type     ENUM('USER_REQUEST','ENROLLMENT_EXCEEDED','COURSE_CANCELLED','INSUFFICIENT_ENROLLMENT') NOT NULL,
    status          ENUM('PENDING','COMPLETED') NOT NULL DEFAULT 'PENDING',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    refunded_at     DATETIME,
    CONSTRAINT fk_refund_payment      FOREIGN KEY (payment_id)      REFERENCES payments(id),
    CONSTRAINT fk_refund_payment_item FOREIGN KEY (payment_item_id) REFERENCES payment_items(id),
    CONSTRAINT fk_refund_user         FOREIGN KEY (user_id)         REFERENCES users(id),
    INDEX idx_refunds_payment (payment_id),
    INDEX idx_refunds_user    (user_id)
);

-- ==============================================
-- 리뷰
-- ==============================================
CREATE TABLE reviews (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    course_id  BIGINT NOT NULL,
    rating     INT    NOT NULL CHECK (rating >= 1 AND rating <= 5),
    content    TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_user   FOREIGN KEY (user_id)   REFERENCES users(id),
    CONSTRAINT fk_review_course FOREIGN KEY (course_id) REFERENCES courses(id),
    UNIQUE KEY unique_review (user_id, course_id),
    INDEX idx_reviews_course (course_id)
);

-- ==============================================
-- 수강 히스토리 (이어보기 — 레슨별 마지막 재생 위치)
-- ==============================================
CREATE TABLE watch_history (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id               BIGINT NOT NULL,
    lesson_id             BIGINT NOT NULL,
    course_id             BIGINT NOT NULL,
    last_position_seconds INT    NOT NULL DEFAULT 0,
    watched_at            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_wh_user   FOREIGN KEY (user_id)   REFERENCES users(id),
    CONSTRAINT fk_wh_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE,
    CONSTRAINT fk_wh_course FOREIGN KEY (course_id) REFERENCES courses(id),
    UNIQUE KEY unique_history (user_id, lesson_id),
    INDEX idx_wh_user_course (user_id, course_id)
);

-- ==============================================
-- 북마크
-- ==============================================
CREATE TABLE bookmarks (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT       NOT NULL,
    lesson_id        BIGINT       NOT NULL,
    position_seconds INT          NOT NULL,
    note             VARCHAR(500),
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bm_user   FOREIGN KEY (user_id)   REFERENCES users(id),
    CONSTRAINT fk_bm_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE,
    INDEX idx_bookmarks_user_lesson (user_id, lesson_id)
);
