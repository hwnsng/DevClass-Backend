package com.hwnsng.devclass.webhook;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "webhook_events", uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "event_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WebhookEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String provider;
    @Column(nullable = false)
    private String eventId;
    @Column(nullable = false)
    private String eventType;
    @Lob
    @Column(nullable = false)
    private String payload;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.RECEIVED;
    private String errorMessage;
    private LocalDateTime receivedAt;
    private LocalDateTime processedAt;

    @PrePersist
    void onCreate() {
        receivedAt = LocalDateTime.now();
    }

    public static WebhookEvent received(String provider, String eventId, String eventType, String payload) {
        WebhookEvent event = new WebhookEvent();
        event.provider = provider;
        event.eventId = eventId;
        event.eventType = eventType;
        event.payload = payload;
        return event;
    }

    public void processed() {
        status = Status.PROCESSED;
        processedAt = LocalDateTime.now();
    }

    public enum Status { RECEIVED, PROCESSED, FAILED }
}
