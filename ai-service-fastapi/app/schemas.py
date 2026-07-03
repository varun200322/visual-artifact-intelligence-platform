from pydantic import BaseModel, Field

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