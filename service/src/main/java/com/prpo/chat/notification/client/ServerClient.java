package com.prpo.chat.notification.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.prpo.chat.notification.dto.ServerDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ServerClient {
    private final RestTemplate restTemplate;

    @Value("${server-service.base-url}")
    private String baseUrl;

    public List<String> getRecipientsInServer(String serverId) {
        try {
            String url = baseUrl + "memberships/users";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Server-Id", serverId);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            return restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<String>>() {}
            ).getBody();
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch server members", e);
        }
    }

    public ServerDto getServerById(String serverId) {
        try {
            String url = baseUrl + "servers/" + serverId;
            return restTemplate.getForObject(url, ServerDto.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch server by id", e);
        }
    }
}
