package com.hwnsng.devclass.payment.controller;

import com.hwnsng.devclass.payment.dto.*;
import com.hwnsng.devclass.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Payment", description = "결제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "결제 준비", description = "orderId와 결제 정보를 반환합니다.")
    @PostMapping("/prepare")
    public ResponseEntity<PreparePaymentResponse> prepare(@RequestBody PreparePaymentRequest req) {
        return ResponseEntity.ok(paymentService.prepare(req));
    }

    @Operation(summary = "결제 승인", description = "토스 결제 승인 후 DB에 저장하고 수강 등록합니다.")
    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirm(@RequestBody ConfirmPaymentRequest req) {
        return ResponseEntity.ok(paymentService.confirm(req));
    }

    @Operation(summary = "내 결제 내역 조회")
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<PaymentResponse>> getMyPayments(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getMyPayments(userId));
    }

    @Operation(summary = "결제 취소/환불", description = "paymentItemId 없으면 전체 취소, 있으면 항목별 취소")
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<PaymentResponse> cancel(
            @PathVariable Long paymentId,
            @RequestBody CancelPaymentRequest req) {
        return ResponseEntity.ok(paymentService.cancel(paymentId, req));
    }
}
