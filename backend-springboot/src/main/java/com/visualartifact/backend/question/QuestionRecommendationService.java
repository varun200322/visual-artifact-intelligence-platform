package com.visualartifact.backend.question;

import com.visualartifact.backend.artifact.Artifact;
import com.visualartifact.backend.embedding.EmbeddingResponse;
import com.visualartifact.backend.embedding.FastApiEmbeddingClient;
import com.visualartifact.backend.llm.FastApiLlmFallbackClient;
import com.visualartifact.backend.llm.GeneratedQuestionResponse;
import com.visualartifact.backend.llm.LlmFallbackQuestionResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class QuestionRecommendationService {

    private static final int DEFAULT_QUESTION_LIMIT = 5;
    private static final int MIN_SQL_QUESTION_COUNT = 5;

    private static final String SQL_SOURCE = "SQL";
    private static final String RAG_SOURCE = "RAG";
    private static final String LLM_FALLBACK_SOURCE = "LLM_FALLBACK";

    private final QuestionTemplateRepository questionTemplateRepository;
    private final RecommendationLogRepository recommendationLogRepository;
    private final FastApiEmbeddingClient fastApiEmbeddingClient;
    private final FastApiLlmFallbackClient fastApiLlmFallbackClient;

    public QuestionRecommendationService(
            QuestionTemplateRepository questionTemplateRepository,
            RecommendationLogRepository recommendationLogRepository,
            FastApiEmbeddingClient fastApiEmbeddingClient,
            FastApiLlmFallbackClient fastApiLlmFallbackClient
    ) {
        this.questionTemplateRepository = questionTemplateRepository;
        this.recommendationLogRepository = recommendationLogRepository;
        this.fastApiEmbeddingClient = fastApiEmbeddingClient;
        this.fastApiLlmFallbackClient = fastApiLlmFallbackClient;
    }

    @Transactional
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

        if (templates.size() < MIN_SQL_QUESTION_COUNT) {
            List<QuestionRecommendationResponse> ragQuestions =
                    recommendRagQuestions(artifact, normalizedArtifactType);

            if (ragQuestions.size() < MIN_SQL_QUESTION_COUNT) {
                return recommendLlmFallbackQuestions(
                        artifact,
                        normalizedArtifactType
                );
            }

            return ragQuestions;
        }

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
                    RAG_SOURCE,
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
                    RAG_SOURCE,
                    rank
            ));
        }

        return responses;
    }

    public List<QuestionRecommendationResponse> recommendLlmFallbackQuestions(
            Artifact artifact,
            String artifactType
    ) {
        String context = buildLlmFallbackContext(artifactType);

        LlmFallbackQuestionResponse llmResponse =
                fastApiLlmFallbackClient.generateQuestions(
                        artifactType,
                        DEFAULT_QUESTION_LIMIT,
                        context
                );

        List<QuestionRecommendationResponse> responses = new ArrayList<>();

        for (int index = 0; index < llmResponse.questions().size(); index++) {
            GeneratedQuestionResponse question = llmResponse.questions().get(index);
            int rank = index + 1;

            RecommendationLog log = RecommendationLog.generatedFallback(
                    artifact,
                    question.questionText(),
                    question.difficulty(),
                    question.skillTag(),
                    question.topicTag(),
                    llmResponse.modelUsed(),
                    llmResponse.promptVersion(),
                    rank
            );

            recommendationLogRepository.save(log);

            responses.add(new QuestionRecommendationResponse(
                    UUID.randomUUID(),
                    question.questionText(),
                    question.difficulty(),
                    question.skillTag(),
                    question.topicTag(),
                    null,
                    LLM_FALLBACK_SOURCE,
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

    private String buildLlmFallbackContext(String artifactType) {
        String normalizedArtifactType = normalizeArtifactType(artifactType);

        return switch (normalizedArtifactType) {
            case "line_graph" ->
                    "The artifact is a line graph. Generate interpretation questions about trends, axes, increases, decreases, peaks, and overall patterns.";
            case "bar_graph" ->
                    "The artifact is a bar graph. Generate interpretation questions about comparisons, categories, highest values, lowest values, and differences.";
            case "pie_chart" ->
                    "The artifact is a pie chart. Generate interpretation questions about proportions, sectors, percentages, and part-whole relationships.";
            default ->
                    "The artifact type is unknown. Generate cautious visual interpretation questions without assuming specific chart values.";
        };
    }

    private String toPgVectorLiteral(List<Double> embedding) {
        return "[" + embedding.stream()
                .map(value -> String.format(Locale.ROOT, "%.8f", value))
                .reduce((left, right) -> left + "," + right)
                .orElse("") + "]";
    }

    private String normalizeArtifactType(String artifactType) {
        if (artifactType == null || artifactType.isBlank()) {
            return "unknown";
        }

        return artifactType.trim().toLowerCase(Locale.ROOT);
    }
}