package com.prpo.chat.notification.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageReceivedNotificationRequest {

    @NotNull
    private String messageId;

    @NotNull
    private String senderId;

    @NotNull
    private String channelId;

    @NotNull
    private String text;
}
