package com.prpo.chat.notification.api.dto;

import lombok.Data;

import java.time.Instant;

import com.prpo.chat.notification.entity.NotificationStatus;
import com.prpo.chat.notification.entity.NotificationType;

@Data
public class NotificationResponse {

    private Long id;

    private Long recipientId;
    private Long senderId;

    private NotificationType type;
    private NotificationStatus status;

    private String text;

    private Long messageId;

    private Instant createdAt;
    private Instant readAt;
}
