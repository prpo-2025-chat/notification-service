package com.prpo.chat.notification.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageReceivedNotificationRequest {

    @NotNull
    private Long messageId;

    @NotNull
    private Long senderId;

    @NotNull
    private Long channelId;

    @NotNull
    private String text;
}
