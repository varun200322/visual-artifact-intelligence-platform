ALTER TABLE question_templates
ADD COLUMN embedding vector(1536);

ALTER TABLE question_templates
ADD COLUMN embedding_model VARCHAR(100);

ALTER TABLE question_templates
ADD COLUMN embedding_updated_at TIMESTAMPTZ;