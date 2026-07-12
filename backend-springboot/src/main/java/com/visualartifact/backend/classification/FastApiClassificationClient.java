package com.visualartifact.backend.classification;

import com.visualartifact.backend.common.AppException;
import com.visualartifact.backend.common.ErrorCode;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.time.Duration;

@Component
public class FastApiClassificationClient {
    private final WebClient aiServiceWebClient;
    private final Duration aiServiceTimeout;

    public FastApiClassificationClient(WebClient aiServiceWebClient, Duration aiServiceTimeout) {
        this.aiServiceWebClient = aiServiceWebClient;
        this.aiServiceTimeout = aiServiceTimeout;
    }

    public AiClassificationResponse classify(Path artifactPath) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", new FileSystemResource(artifactPath));

        try {
            AiClassificationResponse response = aiServiceWebClient
                    .post()
                    .uri("/classify-artifact")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                    .retrieve()
                    .bodyToMono(AiClassificationResponse.class)
                    .timeout(aiServiceTimeout)
                    .onErrorResume(exception-> Mono.error(
                            new AppException(
                                    ErrorCode.AI_SERVICE_UNAVAILABLE,
                                    "Could not classify artifact because AI service is unavailable"
                            )
                    ))
                    .block();

            if(response == null
                || response.artifactType()==null
                || response.confidence() == null
                || response.modelUsed() == null
                || response.latencyMs() == null) {
                throw new AppException(
                        ErrorCode.AI_SERVICE_INVALID_RESPONSE,
                        "AI Service returned incomplete classification response"
                );
            }

            return response;
        } catch(AppException exception) {
            throw exception;
        } catch(Exception exception) {
            throw new AppException(
                    ErrorCode.AI_SERVICE_UNAVAILABLE,
                    "Could not classify artifact because AI service call failed"
            );
        }
    }

}
