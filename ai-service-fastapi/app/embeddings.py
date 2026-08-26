import hashlib
import os
import random
import time
from typing import Any
import httpx

from dotenv import load_dotenv

from app.schemas import EmbeddingResponse

load_dotenv()

EMBEDDING_DIMENSION = 1536

def should_use_mock_embeddings() -> bool:
    use_mock = os.getenv("USE_MOCK_EMBEDDINGS", "true").strip()
    api_key = os.getenv("OPENAI_API_KEY", "").strip()

    return use_mock == "true" or not api_key

async def  create_embedding(text:str)->EmbeddingResponse:
    if should_use_mock_embeddings():
        return create_mock_embedding(text)
    
    return await create_openai_embedding(text)

def create_mock_embedding(text:str) -> EmbeddingResponse:
    start_time = time.perf_counter()

    seed = int(hashlib.sha256(text.encode("utf-8")).hexdigest(),16)%(2**32)
    random_generator = random.Random(seed)

    embedding = [
        random_generator.uniform(-1.0, 1.0)
        for _ in range(EMBEDDING_DIMENSION)
    ]

    latency_ms = int((time.perf_counter()-start_time)*1000)

    return EmbeddingResponse(
        embedding=embedding,
        model_used="mock-embedding-v1",
        dimension=EMBEDDING_DIMENSION,
        latency_ms=latency_ms,
    )

async def create_openai_embedding(text: str)->EmbeddingResponse:
    start_time = time.perf_counter()

    api_key = os.getenv("OPENAI_API_KEY", "").strip()
    model = os.getenv("OPENAI_EMBEDDING_MODEL", "text-embedding-3-small").strip()
    timeout_seconds = float(os.getenv("OPENAI_REQUEST_TIMEOUT_SECONDS", "30"))

    if not api_key:
        return create_mock_embedding(text)

    payload: dict[str, Any] = {
        "model": model,
        "input": text,
    }

    try:
        async with httpx.AsyncClient(timeout=timeout_seconds) as client:
            response = await client.post(
                "https://api.openai.com/v1/embeddings",
                headers = {
                    "Authorization" : f"Bearer {api_key}",
                    "Content-Type": "application/json",
                },
                json = payload,
            )

        response.raise_for_status()

        body = response.json()
        embedding = body["data"][0]["embedding"]

        latency_ms = int((time.perf_counter()-start_time)*1000)

        return EmbeddingResponse(
            embedding=embedding,
            model_used=model,
            dimension=len(embedding),
            latency_ms=latency_ms,
        )
    except Exception as exception:
        print("Embedding service failed:", repr(exception))
        return create_mock_embedding(text)