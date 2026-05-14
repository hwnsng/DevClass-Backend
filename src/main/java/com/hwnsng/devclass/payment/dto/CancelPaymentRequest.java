package com.hwnsng.devclass.payment.dto;

import lombok.Getter;

@Getter
public class CancelPaymentRequest {
    private Long userId;
    private Long paymentItemId; // null이면 전체 결제 취소
    private String reason;
    private String refundType;  // USER_REQUEST / ENROLLMENT_EXCEEDED / COURSE_CANCELLED / INSUFFICIENT_ENROLLMENT
}
