package com.visualartifact.backend.artifact;

import java.time.Instant;
import java.util.UUID;

public record ArtifactUploadResponse(
        UUID artifactId,
        String originalFileName,
        String fileType,
        Long fileSizeBytes,
        String uploadStatus,
        Instant createdAt
) {
}