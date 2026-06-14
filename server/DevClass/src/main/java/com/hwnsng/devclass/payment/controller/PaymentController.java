package com.hwnsng.devclass.payment.controller;

import com.hwnsng.devclass.common.exception.CustomException;
import com.hwnsng.devclass.payment.dto.*;
import com.hwnsng.devclass.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<PreparePaymentResponse> prepare(@RequestBody PreparePaymentRequest req,
                                                          Authentication auth) {
        requireSelf(auth, req.getUserId());
        return ResponseEntity.ok(paymentService.prepare(req));
    }

    @Operation(summary = "결제 승인", description = "토스 결제 승인 후 DB에 저장하고 수강 등록합니다.")
    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirm(@RequestBody ConfirmPaymentRequest req,
                                                   Authentication auth) {
        requireSelf(auth, req.getUserId());
        return ResponseEntity.ok(paymentService.confirm(req));
    }

    @Operation(summary = "내 결제 내역 조회")
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<PaymentResponse>> getMyPayments(@PathVariable Long userId,
                                                               Authentication auth) {
        requireSelf(auth, userId);
        return ResponseEntity.ok(paymentService.getMyPayments(userId));
    }

    @Operation(summary = "결제 취소/환불", description = "paymentItemId 없으면 전체 취소, 있으면 항목별 취소")
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<PaymentResponse> cancel(
            @PathVariable Long paymentId,
            @RequestBody CancelPaymentRequest req,
            Authentication auth) {
        requireSelf(auth, req.getUserId());
        return ResponseEntity.ok(paymentService.cancel(paymentId, req));
    }

    /** 본인 또는 ADMIN만 허용 */
    private void requireSelf(Authentication auth, Long targetUserId) {
        if (auth == null) throw new CustomException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "로그인이 필요합니다.");
        Long principalId = (Long) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !principalId.equals(targetUserId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근 권한이 없습니다.");
        }
    }
}
