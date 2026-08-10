CREATE TABLE recommendation_logs (
    id UUID PRIMARY KEY,
    artifact_id UUID NOT NULL,
    question_template_id UUID,
    recommendation_source VARCHAR(50) NOT NULL,
    rank_position INTEGER NOT NULL,
    relevance_score NUMERIC(5,4),
    created_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_recommendation_logs_artifact
        FOREIGN KEY (artifact_id)
        REFERENCES artifacts(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_recommendation_logs_question_template
        FOREIGN KEY (question_template_id)
        REFERENCES question_templates(id)
        ON DELETE SET NULL
);

CREATE INDEX idx_recommendation_logs_artifact_id
ON recommendation_logs(artifact_id);

CREATE INDEX idx_recommendation_logs_source
ON recommendation_logs(recommendation_source);