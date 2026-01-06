package com.prpo.chat.notification.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SendGridEmailSender {
    private static final String URL = "https://api.sendgrid.com/v3/mail/send";

    private final RestTemplate restTemplate;

    @Value("${sendgrid.api-key}")
    private String apiKey;
    
    @Value("${sendgrid.from}")
    private String from;

    public void send(String to, String subject, String body) {
        Map<String, Object> payload = Map.of(
            "personalizations", List.of(Map.of("to", List.of(Map.of("email", to)))),
            "from", Map.of("email", from),
            "subject", subject,
            "content", List.of(Map.of("type", "text/plain", "value", body))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.postForEntity(URL, new HttpEntity<>(payload, headers), String.class);
    }
}
