package com.prpo.chat.notification.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class WebSocketEventsLogger {

    private static final Logger log = LoggerFactory.getLogger(WebSocketEventsLogger.class);

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String user = accessor.getUser() != null ? accessor.getUser().getName() : "unknown";
        log.info("WS CONNECT user={} session={}", user, accessor.getSessionId());
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String dest = accessor.getDestination();
        String user = accessor.getUser() != null ? accessor.getUser().getName() : "unknown";
        log.info("WS SUBSCRIBE user={} dest={} session={}", user, dest, accessor.getSessionId());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String user = accessor.getUser() != null ? accessor.getUser().getName() : "unknown";
        log.info("WS DISCONNECT user={} session={}", user, accessor.getSessionId());
    }
}
