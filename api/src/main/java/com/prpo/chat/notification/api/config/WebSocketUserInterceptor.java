package com.prpo.chat.notification.api.config;

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
    private static final String USER_ID_HEADER = "user-id";
    private static final String ATTR_USER = "user-id";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
        String userId = accessor.getFirstNativeHeader(USER_ID_HEADER);
        if (userId != null && !userId.isBlank()) {
            String normalized = userId.trim();

            accessor.setUser(() -> normalized);
            accessor.getSessionAttributes().put(ATTR_USER, normalized);
        }
        }

        if (accessor.getUser() == null) {
        Object fromSession = accessor.getSessionAttributes().get(ATTR_USER);
        if (fromSession instanceof String s && !s.isBlank()) {
            accessor.setUser(() -> s);
        }
        }

        return message;
    }
}
