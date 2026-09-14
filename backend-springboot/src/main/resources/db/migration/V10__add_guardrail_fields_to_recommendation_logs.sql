ALTER TABLE recommendation_logs
ADD COLUMN guardrail_status VARCHAR(50);

ALTER TABLE recommendation_logs
ADD COLUMN guardrail_reason TEXT;

ALTER TABLE recommendation_logs
ADD COLUMN human_review_required BOOLEAN DEFAULT FALSE;