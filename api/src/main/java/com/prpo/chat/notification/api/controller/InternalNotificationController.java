package com.prpo.chat.notification.api.controller;

import com.prpo.chat.notification.api.client.EncryptionClient;
import com.prpo.chat.notification.api.dto.MessageReceivedNotificationRequest;
import com.prpo.chat.notification.api.dto.NotificationResponse;
import com.prpo.chat.notification.entity.Notification;
import com.prpo.chat.notification.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/notifications")
@RequiredArgsConstructor
public class InternalNotificationController {

    private final NotificationService notificationService;
    private final EncryptionClient encryptionClient;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/message-received")
    public void handleMessageReceived(
            @Validated @RequestBody MessageReceivedNotificationRequest request
    ) {

        String encrypted = encryptionClient.encrypt(request.getText());

        Notification n = notificationService.createMessageReceivedNotification(
                request.getMessageId(),
                request.getSenderId(),
                request.getRecipientId(),
                encrypted
        );

        NotificationResponse response = mapToResponse(n, request.getText());


        messagingTemplate.convertAndSendToUser(
                request.getRecipientId().toString(),
                "/queue/notifications",
                response
        );
    }

    private NotificationResponse mapToResponse(Notification n, String plainText) {
        NotificationResponse dto = new NotificationResponse();
        dto.setId(n.getId());
        dto.setRecipientId(n.getRecipientId());
        dto.setSenderId(n.getSenderId());
        dto.setType(n.getType());
        dto.setStatus(n.getStatus());
        dto.setMessageId(n.getMessageId());
        dto.setCreatedAt(n.getCreatedAt());
        dto.setReadAt(n.getReadAt());
        dto.setText(plainText); // show decrypted text to FE
        return dto;
    }
}
