package com.prpo.chat.notification.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI notificationServiceOpenAPI() {
    return new OpenAPI()
      .info(new Info()
        .title("Message Service API")
        .version("v1"));
  }
}

