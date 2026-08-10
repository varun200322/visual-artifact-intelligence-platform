package com.visualartifact.backend.artifact;

import com.visualartifact.backend.classification.ArtifactClassificationResponse;
import com.visualartifact.backend.question.QuestionRecommendationResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ArtifactUploadResponse(
        UUID artifactId,
        String originalFileName,
        String fileType,
        Long fileSizeBytes,
        String uploadStatus,
        Instant createdAt,
        ArtifactClassificationResponse classification,
        List<QuestionRecommendationResponse> recommendedQuestions
) {
}