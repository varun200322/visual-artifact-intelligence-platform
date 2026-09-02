from pydantic import BaseModel, Field
from typing import List

class HealthResponse(BaseModel):
    service: str
    status: str
    version: str

class ArtifactClassificationResponse(BaseModel):
    artifact_type: str = Field(..., description = "Detected artifact type")
    confidence: float = Field(..., ge=0.0, le=1.0)
    reasoning_summary: str
    model_used: str
    latency_ms: int

class EmbeddingRequest(BaseModel):
    text: str

class EmbeddingResponse(BaseModel):
    embedding: List[float]
    model_used: str
    dimension: int
    latency_ms: int

class LlmQuestionRequest(BaseModel):
    artifact_type: str
    count: int = Field(..., ge=1, le=10)
    context: str

class GeneratedQuestion(BaseModel):
    question_text: str
    difficulty: str
    skill_tag: str
    topic_tag: str

class LlmQuestionResponse(BaseModel):
    questions: List[GeneratedQuestion]
    model_used: str
    prompt_version: str
    latency_ms: int
