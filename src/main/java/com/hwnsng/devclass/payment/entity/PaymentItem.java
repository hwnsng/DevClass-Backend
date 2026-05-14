package com.hwnsng.devclass.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    private int amount;

    @Enumerated(EnumType.STRING)
    private PaymentItemStatus status = PaymentItemStatus.ACTIVE;

    private String cancelReason;
    private LocalDateTime cancelledAt;

    public static PaymentItem create(Payment payment, Long courseId, int amount) {
        PaymentItem item = new PaymentItem();
        item.payment  = payment;
        item.courseId = courseId;
        item.amount   = amount;
        return item;
    }

    public void cancel(String reason) {
        this.status       = PaymentItemStatus.CANCELLED;
        this.cancelReason = reason;
        this.cancelledAt  = LocalDateTime.now();
    }

    public void refund(String reason) {
        this.status       = PaymentItemStatus.REFUNDED;
        this.cancelReason = reason;
        this.cancelledAt  = LocalDateTime.now();
    }
}
