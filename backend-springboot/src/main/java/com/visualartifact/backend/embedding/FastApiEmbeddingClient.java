package com.visualartifact.backend.embedding;

import com.visualartifact.backend.common.AppException;
import com.visualartifact.backend.common.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class FastApiEmbeddingClient {
    private final WebClient aiServiceWebClient;
    private final Duration aiServiceTimeout;

    public FastApiEmbeddingClient(
            WebClient aiServiceWebClient,
            Duration aiServiceTimeout
    ) {
        this.aiServiceTimeout = aiServiceTimeout;
        this.aiServiceWebClient = aiServiceWebClient;
    }

    public EmbeddingResponse embedText(String text) {
        try {
            EmbeddingResponse response = aiServiceWebClient
                    .post()
                    .uri("/embed-text")
                    .bodyValue(new EmbeddingRequest(text))
                    .retrieve()
                    .bodyToMono(EmbeddingResponse.class)
                    .timeout(aiServiceTimeout)
                    .onErrorResume(exception-> Mono.error(
                            new AppException(
                                    ErrorCode.AI_SERVICE_UNAVAILABLE,
                                    "Could not create embedding because AI service is unavailable"
                            )
                    )).block();

            if(response==null
            || response.embedding()==null
            || response.embedding().isEmpty()
            || response.modelUsed()==null
            || response.dimension() == null) {
                throw new AppException(
                        ErrorCode.AI_SERVICE_INVALID_RESPONSE,
                        "AI Service returned incomplete embedding response"
                );
            }
            return response;
        } catch (AppException exception) {
            throw exception;
        } catch(Exception exception) {
            throw new AppException(
                    ErrorCode.AI_SERVICE_UNAVAILABLE,
                    "Embedding request failed"
            );
        }
    }
}
