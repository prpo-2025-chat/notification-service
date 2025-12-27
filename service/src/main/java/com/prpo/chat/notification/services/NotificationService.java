package com.prpo.chat.notification.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prpo.chat.notification.api.dto.NotificationResponse;
import com.prpo.chat.notification.client.EncryptionClient;
import com.prpo.chat.notification.client.ServerClient;
import com.prpo.chat.notification.entity.Notification;
import com.prpo.chat.notification.entity.NotificationStatus;
import com.prpo.chat.notification.entity.NotificationType;
import com.prpo.chat.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EncryptionClient encryptionClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final ServerClient serverClient;

    @Transactional
    public void handleMessageReceived(
            String messageId,
            String senderId,
            String channelId,
            String text
    ) {
        String encrypted = encryptionClient.encrypt(text);

        List<String> userIds = new ArrayList<>(serverClient.getRecipientsInServer(channelId));
        userIds.remove(senderId);

        for (String recipientId : userIds) {
            Notification n = createMessageReceivedNotification(
                    messageId,
                    senderId,
                    recipientId,
                    channelId,
                    encrypted
            );

            NotificationResponse response = mapToResponse(n, text);

            messagingTemplate.convertAndSendToUser(
                    recipientId,
                    "/queue/notifications",
                    response
            );
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationResponses(String userId, NotificationStatus status) {
        List<Notification> list = getNotifications(userId, status);
        return list.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void markAsRead(String notificationId, String userId) {
        Notification n = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        if (!n.getRecipientId().equals(userId)) {
            throw new IllegalStateException("Notification does not belong to this user");
        }

        if (n.getStatus() == NotificationStatus.UNREAD) {
            n.setStatus(NotificationStatus.READ);
            n.setReadAt(Instant.now());
            notificationRepository.save(n);
        }
    }

    @Transactional
    public Notification createMessageReceivedNotification(
            String messageId,
            String senderId,
            String recipientId,
            String channelId,
            String encryptedPayload
    ) {
        Notification n = new Notification();
        n.setRecipientId(recipientId);
        n.setSenderId(senderId);
        n.setChannelId(channelId);
        n.setType(NotificationType.MESSAGE_RECEIVED);
        n.setStatus(NotificationStatus.UNREAD);
        n.setEncryptedPayload(encryptedPayload);
        n.setMessageId(messageId);
        n.setCreatedAt(Instant.now());

        return notificationRepository.save(n);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotifications(String userId, NotificationStatus status) {
        if (status == null) {
            return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
        }
        return notificationRepository.findByRecipientIdAndStatusOrderByCreatedAtDesc(userId, status);
    }

    private NotificationResponse mapToResponse(Notification n) {
        String plainText = encryptionClient.decrypt(n.getEncryptedPayload());
        return mapToResponse(n, plainText);
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
