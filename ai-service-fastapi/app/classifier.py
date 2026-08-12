import base64
import json
import os
import time
from typing import Any

import httpx
from dotenv import load_dotenv
from fastapi import UploadFile

from app.schemas import ArtifactClassificationResponse

load_dotenv()

SUPPORTED_IMAGE_TYPES = {
    "image/png",
    "image/jpeg",
    "image/webp",
}

ALLOWED_ARTIFACT_TYPES = {
    "line_graph",
    "bar_graph",
    "pie_chart",
    "unknown",
}


def should_use_mock_classifier() -> bool:
    use_mock = os.getenv("USE_MOCK_CLASSIFIER", "true").strip().lower()
    api_key = os.getenv("OPENAI_API_KEY", "").strip()

    return use_mock == "true" or not api_key


async def classify_artifact(file: UploadFile) -> ArtifactClassificationResponse:
    if should_use_mock_classifier():
        return classify_artifact_mock(file)

    return await classify_artifact_with_vision_llm(file)


def classify_artifact_mock(file: UploadFile) -> ArtifactClassificationResponse:
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
        reasoning_summary = "Mock classifier detected 'line' in the file name."
    elif "bar" in filename:
        artifact_type = "bar_graph"
        confidence = 0.85
        reasoning_summary = "Mock classifier detected 'bar' in the file name."
    elif "pie" in filename:
        artifact_type = "pie_chart"
        confidence = 0.85
        reasoning_summary = "Mock classifier detected 'pie' in the file name."
    else:
        artifact_type = "unknown"
        confidence = 0.40
        reasoning_summary = "Mock classifier could not confidently identify the artifact type."

    latency_ms = int((time.perf_counter() - start_time) * 1000)

    return ArtifactClassificationResponse(
        artifact_type=artifact_type,
        confidence=confidence,
        reasoning_summary=reasoning_summary,
        model_used="mock-classifier-v1",
        latency_ms=latency_ms,
    )


async def classify_artifact_with_vision_llm(
    file: UploadFile,
) -> ArtifactClassificationResponse:
    start_time = time.perf_counter()

    try:
        content_type = file.content_type or "application/octet-stream"

        if content_type not in SUPPORTED_IMAGE_TYPES:
            return build_fallback_response(
                start_time=start_time,
                reasoning_summary="Unsupported image content type.",
                model_used="vision-classifier-input-validation",
            )

        image_bytes = await file.read()

        if not image_bytes:
            return build_fallback_response(
                start_time=start_time,
                reasoning_summary="Uploaded image file was empty.",
                model_used="vision-classifier-input-validation",
            )

        api_key = os.getenv("OPENAI_API_KEY", "").strip()

        if not api_key:
            return build_fallback_response(
                start_time=start_time,
                reasoning_summary="OPENAI_API_KEY is not configured.",
                model_used="vision-classifier-missing-api-key",
            )

        model = os.getenv("OPENAI_VISION_MODEL", "gpt-5-mini").strip()
        prompt_version = os.getenv(
            "CLASSIFIER_PROMPT_VERSION",
            "vision-classifier-v1",
        ).strip()
        timeout_seconds = float(os.getenv("OPENAI_REQUEST_TIMEOUT_SECONDS", "30"))

        image_data_url = build_image_data_url(image_bytes, content_type)

        payload = build_openai_payload(
            model=model,
            image_data_url=image_data_url,
            prompt_version=prompt_version,
        )

        async with httpx.AsyncClient(timeout=timeout_seconds) as client:
            response = await client.post(
                "https://api.openai.com/v1/responses",
                headers={
                    "Authorization": f"Bearer {api_key}",
                    "Content-Type": "application/json",
                },
                json=payload,
            )

        try:
            response.raise_for_status()
        except httpx.HTTPStatusError as exception:
            print(
                "OpenAI API HTTP error:",
                exception.response.status_code,
                exception.response.text,
            )

            return build_fallback_response(
                start_time=start_time,
                reasoning_summary=f"Vision classifier failed with OpenAI API status {exception.response.status_code}.",
                model_used=f"{model}:{prompt_version}:http-error",
            )

        response_body = response.json()
        output_text = extract_output_text(response_body)

        parsed = json.loads(output_text)
        validated = normalize_model_response(parsed)

        latency_ms = int((time.perf_counter() - start_time) * 1000)

        return ArtifactClassificationResponse(
            artifact_type=validated["artifact_type"],
            confidence=validated["confidence"],
            reasoning_summary=validated["reasoning_summary"],
            model_used=f"{model}:{prompt_version}",
            latency_ms=latency_ms,
        )

    except httpx.RequestError as exception:
        print("OpenAI API request error:", exception)

        return build_fallback_response(
            start_time=start_time,
            reasoning_summary="Vision classifier could not reach the OpenAI API.",
            model_used="vision-classifier-network-error",
        )

    except Exception as exception:
        print("Vision classifier unexpected error:", repr(exception))

        return build_fallback_response(
            start_time=start_time,
            reasoning_summary="Vision classifier failed because of an unexpected internal error.",
            model_used="vision-classifier-internal-error",
        )


def build_fallback_response(
    start_time: float,
    reasoning_summary: str,
    model_used: str,
) -> ArtifactClassificationResponse:
    latency_ms = int((time.perf_counter() - start_time) * 1000)

    return ArtifactClassificationResponse(
        artifact_type="unknown",
        confidence=0.0,
        reasoning_summary=reasoning_summary,
        model_used=model_used,
        latency_ms=latency_ms,
    )


def build_image_data_url(image_bytes: bytes, content_type: str) -> str:
    encoded_image = base64.b64encode(image_bytes).decode("utf-8")
    return f"data:{content_type};base64,{encoded_image}"


def build_openai_payload(
    model: str,
    image_data_url: str,
    prompt_version: str,
) -> dict[str, Any]:
    return {
        "model": model,
        "input": [
            {
                "role": "user",
                "content": [
                    {
                        "type": "input_text",
                        "text": build_classifier_prompt(prompt_version),
                    },
                    {
                        "type": "input_image",
                        "image_url": image_data_url,
                        "detail": "low",
                    },
                ],
            }
        ],
        "text": {
            "format": {
                "type": "json_schema",
                "name": "artifact_classification",
                "schema": {
                    "type": "object",
                    "additionalProperties": False,
                    "properties": {
                        "artifact_type": {
                            "type": "string",
                            "enum": [
                                "line_graph",
                                "bar_graph",
                                "pie_chart",
                                "unknown",
                            ],
                        },
                        "confidence": {
                            "type": "number",
                            "minimum": 0,
                            "maximum": 1,
                        },
                        "reasoning_summary": {
                            "type": "string",
                            "maxLength": 500,
                        },
                    },
                    "required": [
                        "artifact_type",
                        "confidence",
                        "reasoning_summary",
                    ],
                },
                "strict": True,
            }
        },
    }


def build_classifier_prompt(prompt_version: str) -> str:
    return f"""
You are the visual artifact classifier for an educational GenAI application.

Prompt version: {prompt_version}

Classify the uploaded image into exactly one of these artifact types:

- line_graph
- bar_graph
- pie_chart
- unknown

Use line_graph only when the image clearly contains axes and a line or trend curve.
Use bar_graph only when the image clearly contains rectangular bars comparing categories or values.
Use pie_chart only when the image clearly contains a circular chart divided into sectors.
Use unknown when the artifact is a table, diagram, screenshot, handwritten work, photo, mixed chart, unclear image, or anything outside the supported categories.

Return only structured JSON matching the required schema.
"""


def extract_output_text(response_body: dict[str, Any]) -> str:
    direct_output_text = response_body.get("output_text")

    if isinstance(direct_output_text, str) and direct_output_text.strip():
        return direct_output_text

    output_items = response_body.get("output", [])

    for item in output_items:
        content_items = item.get("content", [])

        for content in content_items:
            text = content.get("text")

            if isinstance(text, str) and text.strip():
                return text

    raise ValueError("OpenAI response did not contain output text")


def normalize_model_response(parsed: dict[str, Any]) -> dict[str, Any]:
    artifact_type = parsed.get("artifact_type", "unknown")
    confidence = parsed.get("confidence", 0.0)
    reasoning_summary = parsed.get("reasoning_summary", "")

    if artifact_type not in ALLOWED_ARTIFACT_TYPES:
        artifact_type = "unknown"

    try:
        confidence = float(confidence)
    except (TypeError, ValueError):
        confidence = 0.0

    confidence = max(0.0, min(1.0, confidence))

    if not isinstance(reasoning_summary, str) or not reasoning_summary.strip():
        reasoning_summary = "The model did not provide a usable reasoning summary."

    return {
        "artifact_type": artifact_type,
        "confidence": confidence,
        "reasoning_summary": reasoning_summary.strip(),
    }