package com.hwnsng.devclass.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwnsng.devclass.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final WebhookEventRepository eventRepository;
    private final ObjectMapper objectMapper;

    @PostMapping("/payment")
    public ResponseEntity<Map<String, String>> receive(
            @RequestHeader(value = "tosspayments-webhook-transmission-id", required = false) String transmissionId,
            @RequestBody String payload) throws Exception {
        JsonNode body = objectMapper.readTree(payload);
        String eventType = body.path("eventType").asText();
        JsonNode payment = body.path("data");
        if (!StringUtils.hasText(transmissionId)
                || !"PAYMENT_STATUS_CHANGED".equals(eventType)
                || !StringUtils.hasText(payment.path("paymentKey").asText())
                || !StringUtils.hasText(payment.path("orderId").asText())
                || !StringUtils.hasText(payment.path("status").asText())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "INVALID_WEBHOOK", "올바르지 않은 토스페이먼츠 웹훅입니다.");
        }
        if (eventRepository.existsByProviderAndEventId("TOSS_PAYMENTS", transmissionId)) {
            return ResponseEntity.ok(Map.of("status", "DUPLICATE"));
        }
        WebhookEvent event = WebhookEvent.received("TOSS_PAYMENTS", transmissionId, eventType, payload);
        event.processed();
        eventRepository.save(event);
        return ResponseEntity.ok(Map.of("status", "PROCESSED"));
    }
}
