from fastapi import FastAPI, File, UploadFile

from app.classifier import classify_artifact_mock
from app.schemas import HealthResponse, ArtifactClassificationResponse

app = FastAPI(
    title = "visual-artifact-AI-service",
    description = "FastAPI service for artifact classification and future AI workflows.",
    version = "0.0.1",
)

@app.get("/health", response_model=HealthResponse)
def health()->HealthResponse:
    return HealthResponse(
        service = "ai-service-fastapi",
        status = "UP",
        version = "0.0.1",
    )

@app.post("/classify-artifact", response_model = ArtifactClassificationResponse)
def classify_artifact(
    file: UploadFile = File(...)
)->ArtifactClassificationResponse:
   return classify_artifact_mock(file)