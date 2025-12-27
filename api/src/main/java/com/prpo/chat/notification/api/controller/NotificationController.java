package com.prpo.chat.notification.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prpo.chat.notification.api.dto.NotificationResponse;
import com.prpo.chat.notification.entity.NotificationStatus;
import com.prpo.chat.notification.services.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification endpoints for clients")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
        summary = "Get notifications for a user",
        description = "Returns notifications for the given user. Optionally filter by notification status."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Notifications retrieved successfully",
            content = @Content(schema = @Schema(implementation = NotificationResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid query parameters")
    })
    @GetMapping
    public List<NotificationResponse> getNotifications(
        @Parameter(description = "Recipient user ID", required = true)
        @RequestParam String userId,

        @Parameter(description = "Optional status filter", required = false)
        @RequestParam(required = false) NotificationStatus status
    ) {
        return notificationService.getNotificationResponses(userId, status);
    }

    @Operation(
        summary = "Mark notification as read",
        description = "Marks a notification as read for a specific user."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notification marked as read (no response body)"),
        @ApiResponse(responseCode = "404", description = "Notification not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @PatchMapping("/{id}/read")
    public void markAsRead(
        @Parameter(description = "Notification ID", required = true)
        @PathVariable String id,

        @Parameter(description = "User performing the action (must match notification recipient)", required = true)
        @RequestParam String userId
    ) {
        notificationService.markAsRead(id, userId);
    }
}
