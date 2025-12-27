package com.prpo.chat.notification.api.config;

import java.security.Principal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

/**
 * Extracts the user id from the STOMP CONNECT native header "user-id"
 * and attaches it as the Principal so convertAndSendToUser can route
 * to specific recipients.
 */
@Component
public class WebSocketUserInterceptor implements ChannelInterceptor {
    private static final Logger log = LoggerFactory.getLogger(WebSocketUserInterceptor.class);
    private static final String USER_ID_HEADER = "user-id";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String userId = Optional.ofNullable(accessor.getFirstNativeHeader(USER_ID_HEADER)).orElse("");
            if (!userId.isBlank()) {
                Principal principal = () -> userId;
                accessor.setUser(principal);
            } else {
                log.warn("STOMP CONNECT missing user-id header; user-specific queues will not receive messages.");
            }
        }
        return message;
    }
}
