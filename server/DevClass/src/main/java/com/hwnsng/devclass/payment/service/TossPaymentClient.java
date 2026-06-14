package com.hwnsng.devclass.payment.service;

import com.hwnsng.devclass.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.Map;

/**
 * 토스페이먼츠 API 클라이언트
 * - 결제 승인: POST https://api.tosspayments.com/v1/payments/confirm
 * - 결제 취소: POST https://api.tosspayments.com/v1/payments/{paymentKey}/cancel
 */
@Component
public class TossPaymentClient {

    private static final String TOSS_BASE = "https://api.tosspayments.com/v1/payments";

    @Value("${toss.secret-key}")
    private String secretKey;

    private final RestClient restClient = RestClient.create();

    private String basicAuth() {
        String credentials = secretKey + ":";
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    /**
     * 결제 승인
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> confirm(String paymentKey, String orderId, int amount) {
        Map<String, Object> body = Map.of(
                "paymentKey", paymentKey,
                "orderId",    orderId,
                "amount",     amount
        );

        try {
            return restClient.post()
                    .uri(TOSS_BASE + "/confirm")
                    .header(HttpHeaders.AUTHORIZATION, basicAuth())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.BAD_GATEWAY, "PAYMENT_CONFIRM_FAILED",
                    "결제 승인에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 결제 취소 (환불)
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> cancel(String paymentKey, String cancelReason, int cancelAmount) {
        Map<String, Object> body = Map.of(
                "cancelReason", cancelReason,
                "cancelAmount", cancelAmount
        );

        try {
            return restClient.post()
                    .uri(TOSS_BASE + "/" + paymentKey + "/cancel")
                    .header(HttpHeaders.AUTHORIZATION, basicAuth())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.BAD_GATEWAY, "PAYMENT_CANCEL_FAILED",
                    "결제 취소에 실패했습니다: " + e.getMessage());
        }
    }
}
