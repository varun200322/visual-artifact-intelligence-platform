package com.visualartifact.backend.llm;

public record LlmFallbackQuestionRequest(
        String artifactType,
        Integer count,
        String context
) {
}
