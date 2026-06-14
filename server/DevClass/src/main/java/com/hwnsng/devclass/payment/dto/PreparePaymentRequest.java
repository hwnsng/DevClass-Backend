package com.hwnsng.devclass.payment.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class PreparePaymentRequest {
    private Long userId;
    private List<Long> courseIds; // 결제할 강의 ID 목록
}
