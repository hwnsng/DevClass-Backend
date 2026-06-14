package com.hwnsng.devclass.payment.dto;

import lombok.Getter;

@Getter
public class ConfirmPaymentRequest {
    private Long userId;
    private String paymentKey;
    private String orderId;
    private int amount;
}
