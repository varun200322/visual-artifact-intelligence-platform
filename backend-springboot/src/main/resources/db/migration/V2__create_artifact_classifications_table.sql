CREATE TABLE artifact_classifications (
    id UUID PRIMARY KEY,
    artifact_id UUID NOT NULL,
    artifact_type VARCHAR(100) NOT NULL,
    confidence NUMERIC(5,4) NOT NULL,
    reasoning_summary VARCHAR(1000) NOT NULL,
    model_used VARCHAR(100) NOT NULL,
    latency_ms INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_artifact_classifications_artifact
        FOREIGN KEY (artifact_id)
        REFERENCES artifacts(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_artifact_classifications_artifact_id
ON artifact_classifications(artifact_id);

CREATE INDEX idx_artifact_classifications_artifact_type
ON artifact_classifications(artifact_type);