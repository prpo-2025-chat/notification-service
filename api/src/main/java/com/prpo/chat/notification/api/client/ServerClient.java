package com.prpo.chat.notification.api.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ServerClient {
    private RestTemplate restTemplate;

    @Value("${server.service.base-url}")
    private String baseUrl;

    public List<Long> getRecipientsInChannel(Long channelId) {
        // String url = baseUrl + "/internal/channels/" + channelId + "/members";
        // Long[] users = restTemplate.getForObject(url, Long[].class);
        // return Arrays.asList(users);
        // TODO: when server service implemented connect it
        return List.of(2L, 3L, 4L);
    }
}

