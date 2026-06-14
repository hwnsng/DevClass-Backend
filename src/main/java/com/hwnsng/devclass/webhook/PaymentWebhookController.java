package com.hwnsng.devclass.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwnsng.devclass.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final WebhookEventRepository eventRepository;
    private final ObjectMapper objectMapper;

    @Value("${webhook.payment-secret:}")
    private String webhookSecret;

    @PostMapping("/payment")
    public ResponseEntity<Map<String, String>> receive(
            @RequestHeader(value = "X-Webhook-Signature", required = false) String signature,
            @RequestBody String payload) throws Exception {
        verifySignature(payload, signature);
        JsonNode body = objectMapper.readTree(payload);
        String eventId = body.path("eventId").asText();
        String eventType = body.path("eventType").asText("PAYMENT_STATUS_CHANGED");
        if (!StringUtils.hasText(eventId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "INVALID_WEBHOOK", "eventId가 필요합니다.");
        }
        if (eventRepository.existsByProviderAndEventId("PAYMENT", eventId)) {
            return ResponseEntity.ok(Map.of("status", "DUPLICATE"));
        }
        WebhookEvent event = WebhookEvent.received("PAYMENT", eventId, eventType, payload);
        event.processed();
        eventRepository.save(event);
        return ResponseEntity.ok(Map.of("status", "PROCESSED"));
    }

    private void verifySignature(String payload, String signature) throws Exception {
        if (!StringUtils.hasText(webhookSecret)) {
            return;
        }
        if (!StringUtils.hasText(signature)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "INVALID_SIGNATURE", "Webhook 서명이 없습니다.");
        }
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] expected = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        byte[] provided;
        try {
            provided = HexFormat.of().parseHex(signature);
        } catch (IllegalArgumentException e) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "INVALID_SIGNATURE", "Webhook 서명이 올바르지 않습니다.");
        }
        if (!MessageDigest.isEqual(expected, provided)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "INVALID_SIGNATURE", "Webhook 서명이 올바르지 않습니다.");
        }
    }
}
