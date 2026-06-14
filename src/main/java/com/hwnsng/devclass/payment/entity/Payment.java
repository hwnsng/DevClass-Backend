package com.hwnsng.devclass.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "toss_payment_key")
    private String tossPaymentKey;

    @Column(name = "toss_order_id", nullable = false)
    private String tossOrderId;

    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public static Payment create(Long userId, int totalAmount, String tossOrderId) {
        Payment p = new Payment();
        p.userId       = userId;
        p.totalAmount  = totalAmount;
        p.tossOrderId  = tossOrderId;
        return p;
    }

    public void confirm(String tossPaymentKey) {
        this.tossPaymentKey = tossPaymentKey;
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    public void addItem(Long courseId, int amount) {
        items.add(PaymentItem.create(this, courseId, amount));
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELLED;
    }

    public void partialCancel() {
        this.status = PaymentStatus.PARTIAL_CANCELLED;
    }
}
