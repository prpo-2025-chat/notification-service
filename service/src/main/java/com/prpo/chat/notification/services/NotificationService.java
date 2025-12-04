package com.prpo.chat.notification.services;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prpo.chat.notification.entity.Notification;
import com.prpo.chat.notification.entity.NotificationStatus;
import com.prpo.chat.notification.entity.NotificationType;
import com.prpo.chat.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification createMessageReceivedNotification(
            Long messageId,
            Long senderId,
            Long recipientId,
            Long channelId,
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
    public List<Notification> getNotifications(Long userId, NotificationStatus status) {
        if (status == null) {
            return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
        }
        return notificationRepository.findByRecipientIdAndStatusOrderByCreatedAtDesc(userId, status);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
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
}
