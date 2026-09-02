import json
import os
import time
from typing import Any

import httpx
from dotenv import load_dotenv

from app.schemas import (
    GeneratedQuestion,
    LlmQuestionRequest,
    LlmQuestionResponse,
)

load_dotenv()


def should_use_mock_llm_fallback() -> bool:
    use_mock = os.getenv("USE_MOCK_LLM_FALLBACK", "true").strip().lower()
    api_key = os.getenv("OPENAI_API_KEY", "").strip()

    return use_mock == "true" or not api_key


async def generate_questions(
    request: LlmQuestionRequest,
) -> LlmQuestionResponse:
    if should_use_mock_llm_fallback():
        return generate_mock_questions(request)

    return await generate_questions_with_llm(request)


def generate_mock_questions(
    request: LlmQuestionRequest,
) -> LlmQuestionResponse:
    start_time = time.perf_counter()

    artifact_type = normalize_artifact_type(request.artifact_type)

    questions: list[GeneratedQuestion] = []

    for index in range(request.count):
        rank = index + 1

        if artifact_type == "line_graph":
            question_text = f"What key trend can be interpreted from the line graph? #{rank}"
            skill_tag = "trend_interpretation"
            topic_tag = "line_graph"
        elif artifact_type == "bar_graph":
            question_text = f"What comparison can be made from the bar graph? #{rank}"
            skill_tag = "comparison"
            topic_tag = "bar_graph"
        elif artifact_type == "pie_chart":
            question_text = f"What proportion is represented in the pie chart? #{rank}"
            skill_tag = "proportion_interpretation"
            topic_tag = "pie_chart"
        else:
            question_text = f"What visual information can be inferred from this artifact? #{rank}"
            skill_tag = "visual_reasoning"
            topic_tag = "unknown_artifact"

        questions.append(
            GeneratedQuestion(
                question_text=question_text,
                difficulty="medium",
                skill_tag=skill_tag,
                topic_tag=topic_tag,
            )
        )

    latency_ms = int((time.perf_counter() - start_time) * 1000)

    return LlmQuestionResponse(
        questions=questions,
        model_used="mock-llm-fallback-v1",
        prompt_version="question-fallback-v1",
        latency_ms=latency_ms,
    )


async def generate_questions_with_llm(
    request: LlmQuestionRequest,
) -> LlmQuestionResponse:
    start_time = time.perf_counter()

    api_key = os.getenv("OPENAI_API_KEY", "").strip()
    model = os.getenv("OPENAI_TEXT_MODEL", "gpt-5-mini").strip()
    prompt_version = os.getenv(
        "LLM_FALLBACK_PROMPT_VERSION",
        "question-fallback-v1",
    ).strip()
    timeout_seconds = float(os.getenv("OPENAI_REQUEST_TIMEOUT_SECONDS", "30"))

    if not api_key:
        return generate_mock_questions(request)

    payload = build_openai_payload(
        request=request,
        model=model,
        prompt_version=prompt_version,
    )

    try:
        async with httpx.AsyncClient(timeout=timeout_seconds) as client:
            response = await client.post(
                "https://api.openai.com/v1/responses",
                headers={
                    "Authorization": f"Bearer {api_key}",
                    "Content-Type": "application/json",
                },
                json=payload,
            )

        response.raise_for_status()

        body = response.json()
        output_text = extract_output_text(body)
        parsed = json.loads(output_text)

        questions = [
            GeneratedQuestion(
                question_text=item["question_text"],
                difficulty=item["difficulty"],
                skill_tag=item["skill_tag"],
                topic_tag=item["topic_tag"],
            )
            for item in parsed.get("questions", [])
        ]

        latency_ms = int((time.perf_counter() - start_time) * 1000)

        return LlmQuestionResponse(
            questions=questions,
            model_used=model,
            prompt_version=prompt_version,
            latency_ms=latency_ms,
        )

    except Exception as exception:
        print("LLM fallback failed:", repr(exception))
        return generate_mock_questions(request)


def build_openai_payload(
    request: LlmQuestionRequest,
    model: str,
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
                        "text": build_prompt(request, prompt_version),
                    }
                ],
            }
        ],
        "text": {
            "format": {
                "type": "json_schema",
                "name": "generated_questions",
                "schema": {
                    "type": "object",
                    "additionalProperties": False,
                    "properties": {
                        "questions": {
                            "type": "array",
                            "minItems": request.count,
                            "maxItems": request.count,
                            "items": {
                                "type": "object",
                                "additionalProperties": False,
                                "properties": {
                                    "question_text": {"type": "string"},
                                    "difficulty": {
                                        "type": "string",
                                        "enum": ["easy", "medium", "hard"],
                                    },
                                    "skill_tag": {"type": "string"},
                                    "topic_tag": {"type": "string"},
                                },
                                "required": [
                                    "question_text",
                                    "difficulty",
                                    "skill_tag",
                                    "topic_tag",
                                ],
                            },
                        }
                    },
                    "required": ["questions"],
                },
                "strict": True,
            }
        },
    }


def build_prompt(
    request: LlmQuestionRequest,
    prompt_version: str,
) -> str:
    return f"""
You are generating educational questions for a visual artifact intelligence platform.

Prompt version: {prompt_version}

Artifact type:
{request.artifact_type}

Context:
{request.context}

Generate exactly {request.count} questions.

Rules:
- Questions must be suitable for interpreting the artifact.
- Do not invent specific values that are not present in the context.
- Do not mention that you are an AI model.
- Avoid duplicate questions.
- Return only structured JSON matching the schema.
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


def normalize_artifact_type(artifact_type: str) -> str:
    normalized = (artifact_type or "").strip().lower()

    if normalized in {"line_graph", "bar_graph", "pie_chart"}:
        return normalized

    return "unknown"