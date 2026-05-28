## Why Evals Matter

This project uses AI components for artifact classification, semantic retrieval, and controlled question generation. Because AI outputs can vary, the system requires evaluation workflows to measure reliability, relevance, and correctness.

Evals are not an optional feature. They are part of the engineering quality process.

## Evaluation Areas

The system will evaluate:

1. Artifact classification quality
2. SQL retrieval relevance
3. RAG retrieval relevance
4. LLM fallback output quality
5. JSON response validity
6. Difficulty alignment
7. Question duplication
8. User feedback quality
9. Latency and cost behavior

## Evaluation Types

### Rule-Based Evals

Used to check deterministic conditions:

- Is the output valid JSON?
- Are the required fields present?
- Are there exactly the requested number of questions?
- Is the artifact type valid?
- Is the difficulty value valid?
- Are duplicate questions present?

### Human-Labeled Evals

Used for quality judgment:

- Is the question relevant to the artifact?
- Is the question educationally useful?
- Is the difficulty level appropriate?
- Is the wording clear?
- Is the output safe and non-misleading?

### Retrieval Evals

Used to compare retrieval methods:

- SQL-only retrieval
- Vector retrieval
- SQL + vector retrieval
- LLM fallback

### Production Evals

Used after deployment:

- Fallback rate
- Average LLM cost
- Average latency
- User feedback score
- Failed response rate
- Invalid JSON rate

## Initial Evaluation Metrics

- Classification success rate
- SQL retrieval hit rate
- RAG retrieval hit rate
- LLM fallback rate
- JSON validity rate
- Duplicate question rate
- Average response latency
- Average token usage
- User satisfaction score

## Evaluation Dataset Plan

The evaluation dataset will contain examples in this format:

```json
{
  "artifact_id": "sample-001",
  "artifact_type": "line_graph",
  "expected_question_skills": ["trend interpretation", "axis reading"],
  "difficulty": "medium",
  "minimum_relevant_questions": 3,
  "notes": "Questions should focus on interpreting increase, decrease, and relationship between variables."
}