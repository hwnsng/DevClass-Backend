package com.hwnsng.devclass.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hwnsng.devclass.notification.entity.Notification;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    @JsonProperty("isRead")
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponse(Notification n) {
        this.id = n.getId();
        this.title = n.getTitle();
        this.message = n.getMessage();
        this.isRead = n.isRead();
        this.createdAt = n.getCreatedAt();
    }
}
