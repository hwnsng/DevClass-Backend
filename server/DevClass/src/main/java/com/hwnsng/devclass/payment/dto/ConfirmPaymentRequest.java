package com.hwnsng.devclass.payment.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class ConfirmPaymentRequest {
    private Long userId;
    private String paymentKey;
    private String orderId;
    private int amount;
    private List<Long> courseIds; // 결제 강의 목록
}
