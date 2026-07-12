package com.visualartifact.backend.classification;

import com.visualartifact.backend.artifact.Artifact;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "artifact_classifications")
public class ArtifactClassification {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artifact_id", nullable = false)
    private Artifact artifact;

    @Column(name = "artifact_type", nullable = false, length = 100)
    private String artifactType;

    @Column(name = "confidence", nullable = false, precision = 5, scale = 4)
    private BigDecimal confidence;

    @Column(name = "reasoning_summary", nullable = false, length = 1000)
    private String reasoningSummary;

    @Column(name = "model_used", nullable = false, length = 100)
    private String modelUsed;

    @Column(name = "latency_ms", nullable = false)
    private Integer latencyMs;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ArtifactClassification() {
    }

    public ArtifactClassification(
            Artifact artifact,
            String artifactType,
            BigDecimal confidence,
            String reasoningSummary,
            String modelUsed,
            Integer latencyMs
    ) {
        this.artifact = artifact;
        this.artifactType = artifactType;
        this.confidence = confidence;
        this.reasoningSummary = reasoningSummary;
        this.modelUsed = modelUsed;
        this.latencyMs = latencyMs;
    }

    @PrePersist
    void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public String getArtifactType() {
        return artifactType;
    }

    public BigDecimal getConfidence() {
        return confidence;
    }

    public String getReasoningSummary() {
        return reasoningSummary;
    }

    public String getModelUsed() {
        return modelUsed;
    }

    public Integer getLatencyMs() {
        return latencyMs;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}