package com.visualartifact.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient aiServiceWebClient(
            WebClient.Builder builder,
            AiServiceProperties aiServiceProperties
    ) {
        return builder
                .baseUrl(aiServiceProperties.baseUrl())
                .build();
    }

    @Bean
    public Duration aiServiceTimeout(AiServiceProperties aiServiceProperties) {
        return Duration.ofMillis(aiServiceProperties.timeoutMs());
    }
}
