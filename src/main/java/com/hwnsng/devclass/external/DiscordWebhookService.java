package com.hwnsng.devclass.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
public class DiscordWebhookService {

    @Value("${discord.webhook-url:}")
    private String webhookUrl;

    private final WebClient webClient = WebClient.create();

    public void send(String message) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.debug("Discord webhook URL not configured, skipping: {}", message);
            return;
        }
        try {
            webClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("content", message))
                    .retrieve()
                    .toBodilessEntity()
                    .subscribe(
                            ok -> log.info("Discord webhook sent"),
                            err -> {
                                log.warn("Discord webhook failed (retry once): {}", err.getMessage());
                                retryOnce(message);
                            }
                    );
        } catch (Exception e) {
            log.error("Discord webhook error: {}", e.getMessage());
        }
    }

    private void retryOnce(String message) {
        try {
            webClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("content", message))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.info("Discord webhook retry succeeded");
        } catch (Exception e) {
            log.error("Discord webhook retry also failed: {}", e.getMessage());
        }
    }

    public void sendCourseCreated(String courseTitle, String instructorName) {
        send(String.format("📚 **새 강의 등록!**\n강의: **%s**\n강사: %s", courseTitle, instructorName));
    }

    public void sendCourseDeleted(String courseTitle, String reason) {
        send(String.format("🗑️ **강의 삭제 알림**\n강의: **%s**\n사유: %s", courseTitle, reason));
    }

    public void sendSchedulerResult(String jobName, String status, String message) {
        String emoji = "SUCCESS".equals(status) ? "✅" : "❌";
        send(String.format("%s **스케줄러: %s**\n상태: %s\n%s", emoji, jobName, status, message));
    }
}
