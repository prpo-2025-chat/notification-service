package com.prpo.chat.notification.api.config;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * Assigns a Principal for WebSocket sessions using a `user-id` header so
 * convertAndSendToUser can route to specific recipients.
 */
@Component
public class WebSocketUserHandshakeHandler extends DefaultHandshakeHandler {

    private static final String USER_ID_HEADER = "user-id";

    @Override
    protected Principal determineUser(@NonNull ServerHttpRequest request, @NonNull WebSocketHandler wsHandler,
                                      @NonNull Map<String, Object> attributes) {
        String userId = request.getHeaders().getFirst(USER_ID_HEADER);
        if (userId == null || userId.isBlank()) {
            // fall back to a random session id to keep connection alive, but user destinations will not match
            userId = UUID.randomUUID().toString();
        }
        final String principalName = userId;
        return () -> principalName;
    }
}
