package com.visualartifact.backend.question;

import com.visualartifact.backend.embedding.EmbeddingResponse;
import com.visualartifact.backend.embedding.FastApiEmbeddingClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class QuestionEmbeddingBackfillService {
    private final QuestionTemplateRepository questionTemplateRepository;
    private final FastApiEmbeddingClient fastApiEmbeddingClient;

    public QuestionEmbeddingBackfillService(
            QuestionTemplateRepository questionTemplateRepository,
            FastApiEmbeddingClient fastApiEmbeddingClient
    ) {
        this.questionTemplateRepository = questionTemplateRepository;
        this.fastApiEmbeddingClient = fastApiEmbeddingClient;
    }

    @Transactional
    public int backfillMissingEmbeddings() {
        List<QuestionTemplate> templates = questionTemplateRepository.findByActiveTrueAndEmbeddingModelIsNullOrderByCreatedAtAsc();

        for(QuestionTemplate template: templates) {
            String embeddingInput = buildEmbeddingInput(template);
            EmbeddingResponse response = fastApiEmbeddingClient.embedText(embeddingInput);
            String vectorLiteral = toPgVectorLiteral(response.embedding());
            questionTemplateRepository.updateEmbedding(
                    template.getId(),
                    vectorLiteral,
                    response.modelUsed()
            );
        }
        return templates.size();
    }

    private String buildEmbeddingInput(QuestionTemplate template) {
        return String.format(
                Locale.ROOT,
                "Artifact type: %s. Difficuty: %s. Skill: %s. Topic: %s. Question: %s",
                template.getArtifactType(),
                template.getDifficulty(),
                template.getSkillTag(),
                template.getTopicTag(),
                template.getQuestionText()
        );
    }

    private String toPgVectorLiteral(List<Double> embedding) {
        return "[" + embedding.stream()
                .map(value->String.format(Locale.ROOT, "%.8f", value))
                .reduce((left, right)->left+ ","+right)
                .orElse("")+"]";
    }
}
