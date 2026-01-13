package com.prpo.chat.notification.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.prpo.chat.notification.dto.PresenceDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PresenceClient {

    private final RestTemplate restTemplate;

    @Value("${presence.service.base-url}")
    private String baseUrl;

    public PresenceDto getUserStatus(String userId) {
        try {
            String url = baseUrl + "/{userId}";
            PresenceDto presence = restTemplate.getForObject(url, PresenceDto.class, userId);

            return presence;
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch presence status", e);
        }
    }
}
