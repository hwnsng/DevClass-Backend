package com.hwnsng.devclass.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refunds")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Refund {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "payment_item_id")
    private Long paymentItemId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private int amount;
    private String reason;

    @Enumerated(EnumType.STRING)
    private RefundType refundType;

    @Enumerated(EnumType.STRING)
    private RefundStatus status = RefundStatus.PENDING;

    private LocalDateTime createdAt;
    private LocalDateTime refundedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public static Refund create(Long paymentId, Long paymentItemId, Long userId,
                                int amount, String reason, RefundType type) {
        Refund r = new Refund();
        r.paymentId     = paymentId;
        r.paymentItemId = paymentItemId;
        r.userId        = userId;
        r.amount        = amount;
        r.reason        = reason;
        r.refundType    = type;
        return r;
    }

    public void complete() {
        this.status     = RefundStatus.COMPLETED;
        this.refundedAt = LocalDateTime.now();
    }
}
