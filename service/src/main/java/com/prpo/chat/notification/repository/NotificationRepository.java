package com.prpo.chat.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prpo.chat.notification.entity.Notification;
import com.prpo.chat.notification.entity.NotificationStatus;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    List<Notification> findByRecipientIdAndStatusOrderByCreatedAtDesc(
            Long recipientId,
            NotificationStatus status
    );
}
