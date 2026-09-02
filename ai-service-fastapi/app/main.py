from fastapi import FastAPI, File, UploadFile

from app.classifier import classify_artifact
from app.schemas import HealthResponse, ArtifactClassificationResponse
from app.embeddings import create_embedding
from app.schemas import EmbeddingRequest, EmbeddingResponse
from app.question_generator import generate_questions
from app.schemas import LlmQuestionRequest, LlmQuestionResponse


app = FastAPI(
    title="Visual Artifact AI Service",
    description="FastAPI service for artifact classification and future AI workflows.",
    version="0.0.1",
)


@app.get("/health", response_model=HealthResponse)
def health() -> HealthResponse:
    return HealthResponse(
        service="ai-service-fastapi",
        status="UP",
        version="0.0.1",
    )


@app.post("/classify-artifact", response_model=ArtifactClassificationResponse)
async def classify_artifact_endpoint(
    file: UploadFile = File(...),
) -> ArtifactClassificationResponse:
    result = await classify_artifact(file)

    if result is None:
        return ArtifactClassificationResponse(
            artifact_type="unknown",
            confidence=0.0,
            reasoning_summary="Classifier returned None. Check classifier.py for a missing return statement.",
            model_used="classifier-debug-fallback",
            latency_ms=0,
        )

    return result

@app.post("/embed-text", response_model=EmbeddingResponse)
async def embed_text(request: EmbeddingRequest,)->EmbeddingResponse:
    return await create_embedding(request.text)

@app.post("/generate-questions", response_model=LlmQuestionResponse)
async def generate_questions_endpoint(request: LlmQuestionRequest,)->LlmQuestionResponse:
    return await generate_questions(request)