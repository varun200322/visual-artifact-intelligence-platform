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

    @Column(name = "generated_question_text")
    private String generatedQuestionText;

    @Column(name = "generated_difficulty", length = 50)
    private String generatedDifficulty;

    @Column(name = "generated_skill_tag", length = 100)
    private String generatedSkillTag;

    @Column(name = "generated_topic_tag", length = 100)
    private String generatedTopicTag;

    @Column(name = "model_used", length = 100)
    private String modelUsed;

    @Column(name = "prompt_version", length = 100)
    private String promptVersion;

    @Column(name = "guardrail_status", length = 50)
    private String guardrailStatus;

    @Column(name = "guardrail_reason")
    private String guardrailReason;

    @Column(name = "human_review_required")
    private Boolean humanReviewRequired;

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

    public static RecommendationLog generatedFallback(
            Artifact artifact,
            String questionText,
            String difficulty,
            String skillTag,
            String topicTag,
            String modelUsed,
            String promptVersion,
            int rankPosition,
            String guardrailStatus,
            String guardrailReason,
            boolean humanReviewRequired
    ) {
        RecommendationLog log = new RecommendationLog(
                artifact,
                null,
                "LLM_FALLBACK",
                rankPosition,
                null
        );

        log.generatedQuestionText = questionText;
        log.generatedDifficulty = difficulty;
        log.generatedSkillTag = skillTag;
        log.generatedTopicTag = topicTag;
        log.modelUsed = modelUsed;
        log.promptVersion = promptVersion;
        log.guardrailStatus = guardrailStatus;
        log.guardrailReason = guardrailReason;
        log.humanReviewRequired = humanReviewRequired;

        return log;
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
