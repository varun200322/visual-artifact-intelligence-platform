package com.visualartifact.backend.llm;

import com.visualartifact.backend.common.AppException;
import com.visualartifact.backend.common.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class FastApiLlmFallbackClient {
    private final WebClient aiServiceWebClient;
    private final Duration aiServiceTimeout;

     public FastApiLlmFallbackClient(
             WebClient aiServiceWebClient,
             Duration aiAiServiceTimeout
     ) {
         this.aiServiceTimeout = aiAiServiceTimeout;
         this.aiServiceWebClient = aiServiceWebClient;
     }
     public LlmFallbackQuestionResponse generateQuestions(
             String artifactType,
             int count,
             String context
     ) {
         try {
             LlmFallbackQuestionResponse response = aiServiceWebClient
                     .post()
                     .uri("/generate-questions")
                     .bodyValue(new LlmFallbackQuestionRequest(
                             artifactType,
                             count,
                             context
                     ))
                     .retrieve()
                     .bodyToMono(LlmFallbackQuestionResponse.class)
                     .timeout(aiServiceTimeout)
                     .onErrorResume(exception-> Mono.error(
                             new AppException(
                                     ErrorCode.AI_SERVICE_UNAVAILABLE,
                                     "Could not generate fallback questions because AI service is unavailable"
                             )
                     )).block();
             if(response==null
             || response.questions()==null
             || response.modelUsed()==null
             || response.questions().isEmpty()) {
                 throw new AppException(
                         ErrorCode.AI_SERVICE_INVALID_RESPONSE,
                         "AI Service returned incomplete LLM fallback response"
                 );
             }
             return response;
         } catch(AppException exception) {
             throw exception;
         } catch(Exception exception) {
             throw new AppException(
                     ErrorCode.AI_SERVICE_INVALID_RESPONSE,
                     "LLM fallback request failed"
             );
         }
     }
}
