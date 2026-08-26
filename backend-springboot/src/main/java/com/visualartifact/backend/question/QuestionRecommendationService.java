package com.visualartifact.backend.question;

import com.visualartifact.backend.artifact.Artifact;
import com.visualartifact.backend.embedding.EmbeddingResponse;
import com.visualartifact.backend.embedding.FastApiEmbeddingClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class QuestionRecommendationService {

    private static final int DEFAULT_QUESTION_LIMIT = 5;
    private static final int MIN_SQL_QUESTION_COUNT = 5;
    private static final String SQL_SOURCE = "SQL";
    private final FastApiEmbeddingClient fastApiEmbeddingClient;

    private final QuestionTemplateRepository questionTemplateRepository;
    private final RecommendationLogRepository recommendationLogRepository;

    public QuestionRecommendationService(
            QuestionTemplateRepository questionTemplateRepository,
            RecommendationLogRepository recommendationLogRepository,
            FastApiEmbeddingClient fastApiEmbeddingClient
    ) {
        this.questionTemplateRepository = questionTemplateRepository;
        this.recommendationLogRepository = recommendationLogRepository;
        this.fastApiEmbeddingClient = fastApiEmbeddingClient;
    }

    public List<QuestionRecommendationResponse> recommendSqlQuestions(
            Artifact artifact,
            String artifactType
    ) {
        String normalizedArtifactType = normalizeArtifactType(artifactType);

        List<QuestionTemplate> templates =
                questionTemplateRepository.findByArtifactTypeAndActiveTrueOrderByCreatedAtAsc(
                        normalizedArtifactType,
                        PageRequest.of(0, DEFAULT_QUESTION_LIMIT)
                );

        List<QuestionRecommendationResponse> responses = new ArrayList<>();

        for (int index = 0; index < templates.size(); index++) {
            QuestionTemplate template = templates.get(index);
            int rank = index + 1;

            RecommendationLog log = new RecommendationLog(
                    artifact,
                    template,
                    SQL_SOURCE,
                    rank,
                    BigDecimal.ONE
            );

            if (templates.size() < MIN_SQL_QUESTION_COUNT) {
                return recommendRagQuestions(artifact, normalizedArtifactType);
            }

            recommendationLogRepository.save(log);

            responses.add(new QuestionRecommendationResponse(
                    template.getId(),
                    template.getQuestionText(),
                    template.getDifficulty(),
                    template.getSkillTag(),
                    template.getTopicTag(),
                    template.getGradeLevel(),
                    SQL_SOURCE,
                    rank
            ));
        }

        return responses;
    }

    private String normalizeArtifactType(String artifactType) {
        if (artifactType == null || artifactType.isBlank()) {
            return "unknown";
        }

        return artifactType.trim().toLowerCase();
    }
    public List<QuestionRecommendationResponse> recommendRagQuestions(
            Artifact artifact,
            String artifactType
    ) {
        String queryText = buildRagQueryText(artifactType);

        EmbeddingResponse embeddingResponse =
                fastApiEmbeddingClient.embedText(queryText);

        String vectorLiteral = toPgVectorLiteral(embeddingResponse.embedding());

        List<QuestionTemplate> templates =
                questionTemplateRepository.findSimilarByEmbedding(
                        vectorLiteral,
                        DEFAULT_QUESTION_LIMIT
                );

        List<QuestionRecommendationResponse> responses = new ArrayList<>();

        for (int index = 0; index < templates.size(); index++) {
            QuestionTemplate template = templates.get(index);
            int rank = index + 1;

            RecommendationLog log = new RecommendationLog(
                    artifact,
                    template,
                    "RAG",
                    rank,
                    null
            );

            recommendationLogRepository.save(log);

            responses.add(new QuestionRecommendationResponse(
                    template.getId(),
                    template.getQuestionText(),
                    template.getDifficulty(),
                    template.getSkillTag(),
                    template.getTopicTag(),
                    template.getGradeLevel(),
                    "RAG",
                    rank
            ));
        }

        return responses;
    }

    private String buildRagQueryText(String artifactType) {
        String normalizedArtifactType = normalizeArtifactType(artifactType);

        return switch (normalizedArtifactType) {
            case "line_graph" ->
                    "Questions for interpreting line graphs, trends, axes, increases, decreases, peaks, and data patterns.";
            case "bar_graph" ->
                    "Questions for interpreting bar graphs, comparing categories, highest values, lowest values, and differences.";
            case "pie_chart" ->
                    "Questions for interpreting pie charts, percentages, sectors, proportions, and part-whole relationships.";
            default ->
                    "Questions for interpreting unknown visual artifacts, visual patterns, symbols, labels, and possible context.";
        };
    }

    private String toPgVectorLiteral(List<Double> embedding) {
        return "[" + embedding.stream()
                .map(value -> String.format(Locale.ROOT, "%.8f", value))
                .reduce((left, right) -> left + "," + right)
                .orElse("") + "]";
    }
}