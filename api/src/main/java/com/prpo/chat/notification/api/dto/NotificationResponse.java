package com.prpo.chat.notification.api.dto;

import java.time.Instant;

import com.prpo.chat.notification.entity.NotificationStatus;
import com.prpo.chat.notification.entity.NotificationType;

import lombok.Data;

@Data
public class NotificationResponse {

    private String id;

    private String recipientId;
    private String senderId;
    private String channelId;

    private NotificationType type;
    private NotificationStatus status;

    private String text;

    private String messageId;

    private Instant createdAt;
    private Instant readAt;
}
