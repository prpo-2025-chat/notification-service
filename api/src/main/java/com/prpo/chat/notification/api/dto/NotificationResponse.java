package com.prpo.chat.notification.api.dto;

import java.time.Instant;

import com.prpo.chat.notification.entity.NotificationStatus;
import com.prpo.chat.notification.entity.NotificationType;

import lombok.Data;

@Data
public class NotificationResponse {

    private Long id;

    private Long recipientId;
    private Long senderId;
    private long channelId;

    private NotificationType type;
    private NotificationStatus status;

    private String text;

    private Long messageId;

    private Instant createdAt;
    private Instant readAt;
}
