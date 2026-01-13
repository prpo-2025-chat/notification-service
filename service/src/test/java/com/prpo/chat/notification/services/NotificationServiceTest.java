package com.prpo.chat.notification.services;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.prpo.chat.notification.client.EncryptionClient;
import com.prpo.chat.notification.client.PresenceClient;
import com.prpo.chat.notification.client.SendGridEmailSender;
import com.prpo.chat.notification.client.ServerClient;
import com.prpo.chat.notification.client.UserClient;
import com.prpo.chat.notification.entity.Notification;
import com.prpo.chat.notification.entity.NotificationStatus;
import com.prpo.chat.notification.repository.NotificationRepository;

class NotificationServiceTest {

    @Test
    void markAsRead_updatesStatusAndReadAt() {
        NotificationRepository repository = mock(NotificationRepository.class);
        EncryptionClient encryptionClient = mock(EncryptionClient.class);
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        PresenceClient presenceClient = mock(PresenceClient.class);
        ServerClient serverClient = mock(ServerClient.class);
        UserClient userClient = mock(UserClient.class);
        SendGridEmailSender emailSender = mock(SendGridEmailSender.class);

        NotificationService service = new NotificationService(
            repository,
            encryptionClient,
            messagingTemplate,
            presenceClient,
            serverClient,
            userClient,
            emailSender
        );

        Notification notification = new Notification();
        notification.setRecipientId("user-1");
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setCreatedAt(Instant.now());

        when(repository.findById("notif-1")).thenReturn(Optional.of(notification));

        service.markAsRead("notif-1", "user-1");

        assertEquals(NotificationStatus.READ, notification.getStatus());
        assertNotNull(notification.getReadAt());
        verify(repository).save(notification);
    }
}
