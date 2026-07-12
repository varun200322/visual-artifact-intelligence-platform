package com.visualartifact.backend.classification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ArtifactClassificationResponse(
        UUID classificationId,
        String artifactType,
        BigDecimal confidence,
        String reasoningSummary,
        String modelUsed,
        Integer latencyMs,
        Instant createdAt
) {
}
