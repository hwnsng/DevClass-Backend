package com.hwnsng.devclass.subscription.controller;

import com.hwnsng.devclass.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    /** 강사 구독 */
    @PostMapping
    public ResponseEntity<Void> subscribe(@RequestBody Map<String, Long> body) {
        subscriptionService.subscribe(body.get("userId"), body.get("instructorId"));
        return ResponseEntity.noContent().build();
    }

    /** 구독 취소 */
    @DeleteMapping
    public ResponseEntity<Void> unsubscribe(@RequestParam Long userId, @RequestParam Long instructorId) {
        subscriptionService.unsubscribe(userId, instructorId);
        return ResponseEntity.noContent().build();
    }

    /** 구독 여부 확인 */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkSubscription(
            @RequestParam Long userId, @RequestParam Long instructorId) {
        boolean subscribed = subscriptionService.isSubscribed(userId, instructorId);
        return ResponseEntity.ok(Map.of("subscribed", subscribed));
    }
}
