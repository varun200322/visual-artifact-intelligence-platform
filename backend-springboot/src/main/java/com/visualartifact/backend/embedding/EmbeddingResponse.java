package com.visualartifact.backend.embedding;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record EmbeddingResponse(
        List<Double> embedding,

        @JsonProperty("model_used")
        String modelUsed,

        @JsonProperty("latency_ms")
        Integer latencyMs,

        Integer dimension
) {
}
