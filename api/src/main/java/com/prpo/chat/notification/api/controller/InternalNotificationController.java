package com.prpo.chat.notification.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prpo.chat.notification.api.client.EncryptionClient;
import com.prpo.chat.notification.api.client.ServerClient;
import com.prpo.chat.notification.api.dto.MessageReceivedNotificationRequest;
import com.prpo.chat.notification.api.dto.NotificationResponse;
import com.prpo.chat.notification.entity.Notification;
import com.prpo.chat.notification.services.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/notifications")
@RequiredArgsConstructor
@Tag(name = "Internal Notifications", description = "Internal endpoints used by other services (not intended for FE)")
public class InternalNotificationController {

    private final NotificationService notificationService;
    private final EncryptionClient encryptionClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final ServerClient serverClient;

    @Operation(
        summary = "Handle message-received event",
        description = "Internal endpoint called by other services when a new message is created. " +
                      "Creates notifications for channel recipients (excluding the sender) and pushes them via WebSocket."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notifications created and dispatched (no response body)"),
        @ApiResponse(responseCode = "400", description = "Invalid request payload"),
        @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PostMapping("/message-received")
    public void handleMessageReceived(
        @Parameter(description = "Message-received payload", required = true)
        @Validated @RequestBody MessageReceivedNotificationRequest request
    ) {
        String encrypted = encryptionClient.encrypt(request.getText());

        List<String> userIds = new ArrayList<>(serverClient.getRecipientsInChannel(request.getChannelId()));

        userIds.remove(request.getSenderId());

        for (String recipientId : userIds) {

            Notification n = notificationService.createMessageReceivedNotification(
                request.getMessageId(),
                request.getSenderId(),
                recipientId,
                request.getChannelId(),
                encrypted
            );

            NotificationResponse response = mapToResponse(n, request.getText());

            messagingTemplate.convertAndSendToUser(
                recipientId,
                "/queue/notifications",
                response
            );
        }
    }

    private NotificationResponse mapToResponse(Notification n, String plainText) {
        NotificationResponse dto = new NotificationResponse();
        dto.setId(n.getId());
        dto.setRecipientId(n.getRecipientId());
        dto.setSenderId(n.getSenderId());
        dto.setChannelId(n.getChannelId());
        dto.setType(n.getType());
        dto.setStatus(n.getStatus());
        dto.setMessageId(n.getMessageId());
        dto.setCreatedAt(n.getCreatedAt());
        dto.setReadAt(n.getReadAt());
        dto.setText(plainText);
        return dto;
    }
}
