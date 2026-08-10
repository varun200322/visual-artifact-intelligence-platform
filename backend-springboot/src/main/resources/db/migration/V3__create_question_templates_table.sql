CREATE TABLE question_templates (
    id UUID PRIMARY KEY,
    artifact_type VARCHAR(100) NOT NULL,
    question_text VARCHAR(1000) NOT NULL,
    difficulty VARCHAR(50) NOT NULL,
    skill_tag VARCHAR(100) NOT NULL,
    topic_tag VARCHAR(100) NOT NULL,
    grade_level INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_question_templates_artifact_type
ON question_templates(artifact_type);

CREATE INDEX idx_question_templates_difficulty
ON question_templates(difficulty);

CREATE INDEX idx_question_templates_is_active
ON question_templates(is_active);