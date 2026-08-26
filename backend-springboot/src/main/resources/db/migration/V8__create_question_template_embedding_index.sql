CREATE INDEX idx_question_templates_embedding_cosine
ON question_templates
USING hnsw (embedding vector_cosine_ops)
WHERE embedding IS NOT NULL;