package com.prpo.chat.notification.api.controller;

import com.prpo.chat.notification.api.client.EncryptionClient;
import com.prpo.chat.notification.api.dto.NotificationResponse;
import com.prpo.chat.notification.entity.Notification;
import com.prpo.chat.notification.entity.NotificationStatus;
import com.prpo.chat.notification.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final EncryptionClient encryptionClient;

    @GetMapping
    public List<NotificationResponse> getNotifications(
            @RequestParam Long userId,
            @RequestParam(required = false) NotificationStatus status
    ) {
        List<Notification> list = notificationService.getNotifications(userId, status);
        return list.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @PatchMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id, @RequestParam Long userId) {
        notificationService.markAsRead(id, userId);
    }

    private NotificationResponse mapToResponse(Notification n) {
        NotificationResponse dto = new NotificationResponse();
        dto.setId(n.getId());
        dto.setRecipientId(n.getRecipientId());
        dto.setSenderId(n.getSenderId());
        dto.setType(n.getType());
        dto.setStatus(n.getStatus());
        dto.setMessageId(n.getMessageId());
        dto.setCreatedAt(n.getCreatedAt());
        dto.setReadAt(n.getReadAt());

        // decrypt payload (currently a no-op in EncryptionClient)
        String plainText = encryptionClient.decrypt(n.getEncryptedPayload());
        dto.setText(plainText);

        return dto;
    }
}
