package com.visualartifact.backend.question;

import com.visualartifact.backend.artifact.Artifact;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "recommendation_logs")
public class RecommendationLog {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artifact_id", nullable = false)
    private Artifact artifact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_template_id")
    private QuestionTemplate questionTemplate;

    @Column(name = "recommendation_source", nullable = false, length = 50)
    private String recommendationSource;

    @Column(name = "rank_position", nullable = false)
    private Integer rankPosition;

    @Column(name = "relevance_score", precision = 5, scale = 4)
    private BigDecimal relevanceScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected RecommendationLog() {
    }

    public RecommendationLog(
            Artifact artifact,
            QuestionTemplate questionTemplate,
            String recommendationSource,
            Integer rankPosition,
            BigDecimal relevanceScore
    ) {
        this.artifact = artifact;
        this.questionTemplate = questionTemplate;
        this.recommendationSource = recommendationSource;
        this.rankPosition = rankPosition;
        this.relevanceScore = relevanceScore;
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
}
