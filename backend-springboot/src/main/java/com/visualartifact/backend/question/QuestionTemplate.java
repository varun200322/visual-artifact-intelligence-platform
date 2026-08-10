package com.visualartifact.backend.question;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "question_templates")
@Getter
@Setter
public class QuestionTemplate {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "question_text", nullable = false, length = 100)
    private String questionText;

    @Column(name = "artifact_type", nullable = false, length = 100)
    private String artifactType;

    @Column(nullable = false, length = 50)
    private String difficulty;

    @Column(name = "skill_tag", nullable = false, length = 100)
    private String skillTag;

    @Column(name = "topic_tag", nullable = false, length = 100)
    private String topicTag;

    @Column(name = "grade_level")
    private Integer gradeLevel;

    @Column(name = "is_active", nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected QuestionTemplate() {
    }

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();

        if (id == null) {
            id = UUID.randomUUID();
        }

        if (createdAt == null) {
            createdAt = now;
        }

        if (active == null) {
            active = true;
        }

        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
