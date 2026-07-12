package com.visualartifact.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ai-service")
public record AiServiceProperties(
        String baseUrl,
        long timeoutMs
) {

}
