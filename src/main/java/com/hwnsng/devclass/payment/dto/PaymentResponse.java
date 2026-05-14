package com.hwnsng.devclass.payment.dto;

import com.hwnsng.devclass.payment.entity.Payment;
import com.hwnsng.devclass.payment.entity.PaymentItem;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PaymentResponse {
    private final Long paymentId;
    private final Long userId;
    private final int totalAmount;
    private final String status;
    private final String tossOrderId;
    private final LocalDateTime paidAt;
    private final LocalDateTime createdAt;
    private final List<ItemInfo> items;

    public PaymentResponse(Payment payment) {
        this.paymentId   = payment.getId();
        this.userId      = payment.getUserId();
        this.totalAmount = payment.getTotalAmount();
        this.status      = payment.getStatus().name();
        this.tossOrderId = payment.getTossOrderId();
        this.paidAt      = payment.getPaidAt();
        this.createdAt   = payment.getCreatedAt();
        this.items       = payment.getItems().stream()
                .map(ItemInfo::new).toList();
    }

    @Getter
    public static class ItemInfo {
        private final Long paymentItemId;
        private final Long courseId;
        private final int amount;
        private final String status;

        public ItemInfo(PaymentItem item) {
            this.paymentItemId = item.getId();
            this.courseId      = item.getCourseId();
            this.amount        = item.getAmount();
            this.status        = item.getStatus().name();
        }
    }
}
