package com.visualartifact.backend.question;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface QuestionTemplateRepository extends JpaRepository<QuestionTemplate, UUID> {
    List<QuestionTemplate> findByArtifactTypeAndActiveTrueOrderByCreatedAtAsc(
            String artifactType,
            Pageable pageable
    );

    List<QuestionTemplate> findByActiveTrueAndEmbeddingModelIsNullOrderByCreatedAtAsc();

    @Modifying
    @Query(
            value = """
                    UPDATE question_templates
                    SET embedding = CAST(:embedding AS vector),
                        embedding_model = :embeddingModel,
                        embedding_updated_at = NOW()
                    """,
            nativeQuery = true
    )
    void updateEmbedding(
            @Param("questionId") UUID questionId,
            @Param("embedding") String embedding,
            @Param("embeddingModel") String embeddingModel
    );

    @Query(
            value = """
                SELECT *
                FROM question_templates
                WHERE is_active = TRUE
                  AND embedding IS NOT NULL
                ORDER BY embedding <=> CAST(:queryEmbedding AS vector)
                LIMIT :limit
                """,
            nativeQuery = true
    )
    List<QuestionTemplate> findSimilarByEmbedding(
            @Param("queryEmbedding") String queryEmbedding,
            @Param("limit") int limit
    );
}
