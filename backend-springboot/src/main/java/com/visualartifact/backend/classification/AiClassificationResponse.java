package com.visualartifact.backend.classification;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record AiClassificationResponse(
        @JsonProperty("artifact_type")
        String artifactType,

        BigDecimal confidence,

        @JsonProperty("reasoning_summary")
        String reasoningSummary,

        @JsonProperty("model_used")
        String modelUsed,

        @JsonProperty("latency_ms")
        Integer latencyMs
) {
}
