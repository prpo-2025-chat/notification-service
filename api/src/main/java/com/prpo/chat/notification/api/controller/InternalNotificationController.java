package com.prpo.chat.notification.api.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prpo.chat.notification.api.dto.MessageReceivedNotificationRequest;
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
        notificationService.handleMessageReceived(
                request.getMessageId(),
                request.getSenderId(),
                request.getChannelId(),
                request.getText()
        );
    }
}
