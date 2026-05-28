# Visual Artifact Intelligence Platform

A production-style multimodal AI web application that accepts uploaded visual artifacts such as graphs, charts, tables, and diagrams, classifies the artifact type, and recommends educational questions using a reliability-first retrieval pipeline.

The system follows a SQL-first, RAG-second, LLM-fallback-last architecture.

## Core Idea

Users upload a visual artifact. The backend validates and stores the upload, sends it to an AI service for artifact classification, retrieves relevant educational questions from a structured PostgreSQL question bank, applies semantic retrieval when exact matches are insufficient, and uses controlled LLM fallback only when internal retrieval cannot produce enough relevant questions.

## Architecture Overview

The platform is composed of:

- React.js frontend
- Java Spring Boot backend
- Python FastAPI AI service
- PostgreSQL database
- pgvector-based semantic retrieval
- Object storage for uploaded artifacts
- Controlled LLM fallback layer
- Evaluation and observability workflows

## Retrieval Strategy

The system uses a reliability-first retrieval pipeline:

1. SQL retrieval from curated question templates
2. Vector-based semantic retrieval when SQL is insufficient
3. LLM fallback only when retrieval quality is weak

## Key Engineering Goals

- Secure image upload
- Clean Spring Boot layered architecture
- AI service separation using FastAPI
- SQL-backed question bank
- RAG-based semantic retrieval
- Controlled LLM fallback
- Structured response validation
- Evaluation-first AI workflow
- User feedback capture
- Observability readiness
- Dockerized deployment

## Project Status

Current stage: Architecture and repository setup.

## Repository Structure

```text
backend-springboot/     Java Spring Boot backend
ai-service-fastapi/     Python FastAPI AI service
frontend-react/         React.js frontend
infra/                  Docker, monitoring, and deployment setup
evals/                  Evaluation datasets, rubrics, scripts, and reports
docs/                   Architecture, API contracts, schema, security, and deployment documentation