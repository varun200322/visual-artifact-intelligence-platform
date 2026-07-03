import time
from fastapi import UploadFile

from app.schemas import ArtifactClassificationResponse

"""
Service Contract before adding expensive AI calls.
"""

SUPPORTED_IMAGE_TYPES = {
    "image/png",
    "image/jpeg",
    "image/webp",
}

def classify_artifact_mock(file: UploadFile)->ArtifactClassificationResponse:
    start_time = time.perf_counter()

    filename = (file.filename or "").lower()
    content_type = file.content_type or "unknown"

    if content_type not in SUPPORTED_IMAGE_TYPES:
        artifact_type = "unknown"
        confidence = 0.0
        reasoning_summary = "Unsupported or unknown image content type."
    elif "line" in filename:
        artifact_type = "line_graph"
        confidence = 0.85
        reasoning_summary = "Unsupported or unknown image content type."
    elif "bar" in filename:
        artifact_type = "bar_graph"
        confidence = 0.85
        reasoning_summary = "Mock classifier detected 'pie' in the file name."
    elif "pie" in filename:
        artifact_type = "pie_chart"
        confidence = 0.85
        reasoning_summary = "Mock classifier detected 'pie' in the file name."
    else:
        artifact_type = "unknown"
        confidence = 0.40
        reasoning_summary = "Mock classifier could not confidently identify the artifact type."

    latency_ms = int((time.perf_counter() - start_time)*1000)

    return ArtifactClassificationResponse(
        artifact_type = artifact_type,
        confidence = confidence,
        reasoning_summary = reasoning_summary,
        model_used = "mock-classifier-v1",
        latency_ms = latency_ms,
    )