package com.prpo.chat.notification.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "com.prpo.chat.notification.api",
    "com.prpo.chat.notification.services",
    "com.prpo.chat.notification.repository",
    "com.prpo.chat.notification.entity"
})
@EnableJpaRepositories(basePackages = "com.prpo.chat.notification.repository")
@EntityScan(basePackages = "com.prpo.chat.notification.entity")
public class NotificationApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }
}
