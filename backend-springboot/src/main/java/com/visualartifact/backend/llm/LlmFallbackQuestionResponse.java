package com.visualartifact.backend.llm;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record LlmFallbackQuestionResponse(
        List<GeneratedQuestionResponse> questions,

        @JsonProperty("model_used")
        String modelUsed,

        @JsonProperty("prompt_version")
        String promptVersion,

        @JsonProperty("latency_ms")
        Integer latencyMs
) {
}
