package com.visualartifact.backend.question;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface QuestionTemplateRepository extends JpaRepository<QuestionTemplate, UUID> {
    List<QuestionTemplate> findByArtifactTypeAndActiveTrueOrderByCreatedAtAsc(
            String artifactType,
            Pageable pageable
    );
}
