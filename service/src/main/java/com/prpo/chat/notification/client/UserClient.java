package com.prpo.chat.notification.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.prpo.chat.notification.dto.UserDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserClient {

    private final RestTemplate restTemplate;

    @Value("${user-service.base-url}")
    private String baseUrl;

    public UserDto getUserById(String userId) {
        try {
            String url = baseUrl + userId;
            return restTemplate.getForObject(url, UserDto.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch user by id", e);
        }
    }
}
