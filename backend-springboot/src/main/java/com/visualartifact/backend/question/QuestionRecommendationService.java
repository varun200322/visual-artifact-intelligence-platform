package com.visualartifact.backend.question;

import com.visualartifact.backend.artifact.Artifact;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionRecommendationService {

    private static final int DEFAULT_QUESTION_LIMIT = 5;
    private static final String SQL_SOURCE = "SQL";

    private final QuestionTemplateRepository questionTemplateRepository;
    private final RecommendationLogRepository recommendationLogRepository;

    public QuestionRecommendationService(
            QuestionTemplateRepository questionTemplateRepository,
            RecommendationLogRepository recommendationLogRepository
    ) {
        this.questionTemplateRepository = questionTemplateRepository;
        this.recommendationLogRepository = recommendationLogRepository;
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
}