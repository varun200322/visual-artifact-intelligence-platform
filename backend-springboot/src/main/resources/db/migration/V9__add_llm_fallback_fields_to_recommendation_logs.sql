ALTER TABLE recommendation_logs
ADD COLUMN generated_question_text TEXT;

ALTER TABLE recommendation_logs
ADD COLUMN generated_difficulty VARCHAR(50);

ALTER TABLE recommendation_logs
ADD COLUMN generated_skill_tag VARCHAR(100);

ALTER TABLE recommendation_logs
ADD COLUMN generated_topic_tag VARCHAR(100);

ALTER TABLE recommendation_logs
ADD COLUMN model_used VARCHAR(100);

ALTER TABLE recommendation_logs
ADD COLUMN prompt_version VARCHAR(100);