package com.prpo.chat.notification.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prpo.chat.notification.api.dto.NotificationResponse;
import com.prpo.chat.notification.client.EncryptionClient;
import com.prpo.chat.notification.client.SendGridEmailSender;
import com.prpo.chat.notification.client.ServerClient;
import com.prpo.chat.notification.client.UserClient;
import com.prpo.chat.notification.dto.ServerDto;
import com.prpo.chat.notification.dto.UserDto;
import com.prpo.chat.notification.entity.Notification;
import com.prpo.chat.notification.entity.NotificationStatus;
import com.prpo.chat.notification.entity.NotificationType;
import com.prpo.chat.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final EncryptionClient encryptionClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry userRegistry;
    private final ServerClient serverClient;
    private final UserClient userClient;
    private final SendGridEmailSender emailSender;

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

            if (isUserOnline(recipientId)) {
                try {
                    log.info("Dispatching notification {} to user {} on /queue/notifications", n.getId(), recipientId);
                    messagingTemplate.convertAndSendToUser(
                            recipientId,
                            "/queue/notifications",
                            response
                    );
                    messagingTemplate.convertAndSend(
                            "/topic/notifications." + recipientId,
                            response
                    );
                } catch (Exception e) {
                    // Keep the persisted notification; just log the dispatch failure.
                    log.warn("Failed to push notification to user {} via STOMP: {}", recipientId, e.getMessage());
                    sendEmailFallback(recipientId, senderId, channelId, text);
                }
            } else {
                log.info("User {} is offline; sending email notification.", recipientId);
                sendEmailFallback(recipientId, senderId, channelId, text);
            }
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

    private boolean isUserOnline(String userId) {
        var user = userRegistry.getUser(userId);
        return user != null && !user.getSessions().isEmpty();
    }

    private void sendEmailFallback(String recipientId, String senderId, String channelId, String text) {
        try {
            UserDto user = userClient.getUserById(recipientId);
            if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
                log.warn("Missing email for user {}; skipping email fallback.", recipientId);
                return;
            }

            String channelName = resolveChannelName(channelId);
            String subject = "New message in " + (channelName == null ? channelId : channelName);
            String body = "You have a new message from user " + user.getUsername() + ":\n\n" + text;
            emailSender.send(user.getEmail(), subject, body);
        } catch (Exception e) {
            log.warn("Failed to send email notification to user {}: {}", recipientId, e.getMessage());
        }
    }

    private String resolveChannelName(String channelId) {
        try {
            ServerDto server = serverClient.getServerById(channelId);
            if (server == null || server.getName() == null || server.getName().isBlank()) {
                return null;
            }
            return server.getName();
        } catch (Exception e) {
            log.warn("Failed to resolve channel name for {}: {}", channelId, e.getMessage());
            return null;
        }
    }
}
