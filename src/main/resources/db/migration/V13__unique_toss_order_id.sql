ALTER TABLE payments
    ADD CONSTRAINT uk_payments_toss_order_id UNIQUE (toss_order_id);
