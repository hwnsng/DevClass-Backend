package com.hwnsng.devclass.payment.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class PreparePaymentResponse {
    private final String orderId;
    private final String orderName;
    private final int totalAmount;
    private final List<PaymentCourseItem> items;

    public PreparePaymentResponse(String orderId, String orderName,
                                  int totalAmount, List<PaymentCourseItem> items) {
        this.orderId     = orderId;
        this.orderName   = orderName;
        this.totalAmount = totalAmount;
        this.items       = items;
    }

    @Getter
    public static class PaymentCourseItem {
        private final Long courseId;
        private final String title;
        private final int price;

        public PaymentCourseItem(Long courseId, String title, int price) {
            this.courseId = courseId;
            this.title    = title;
            this.price    = price;
        }
    }
}
