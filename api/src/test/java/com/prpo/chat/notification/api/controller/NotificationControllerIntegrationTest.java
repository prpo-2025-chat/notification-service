package com.prpo.chat.notification.api.controller;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prpo.chat.notification.api.dto.NotificationResponse;
import com.prpo.chat.notification.entity.NotificationStatus;
import com.prpo.chat.notification.entity.NotificationType;
import com.prpo.chat.notification.services.NotificationService;

@WebMvcTest(NotificationController.class)
class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    void getNotifications_returnsServiceResponse() throws Exception {
        NotificationResponse response = new NotificationResponse();
        response.setId(42L);
        response.setRecipientId("user-1");
        response.setSenderId("user-2");
        response.setChannelId("channel-3");
        response.setType(NotificationType.MESSAGE_RECEIVED);
        response.setStatus(NotificationStatus.UNREAD);
        response.setText("hello");
        response.setMessageId("msg-1");
        response.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));

        when(notificationService.getNotificationResponses("user-1", NotificationStatus.UNREAD))
            .thenReturn(List.of(response));

        mockMvc.perform(get("/notifications")
                .param("userId", "user-1")
                .param("status", "UNREAD"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(42))
            .andExpect(jsonPath("$[0].recipientId").value("user-1"))
            .andExpect(jsonPath("$[0].senderId").value("user-2"))
            .andExpect(jsonPath("$[0].channelId").value("channel-3"))
            .andExpect(jsonPath("$[0].type").value("MESSAGE_RECEIVED"))
            .andExpect(jsonPath("$[0].status").value("UNREAD"))
            .andExpect(jsonPath("$[0].text").value("hello"))
            .andExpect(jsonPath("$[0].messageId").value("msg-1"))
            .andExpect(jsonPath("$[0].createdAt").value("2024-01-01T00:00:00Z"));
    }
}
