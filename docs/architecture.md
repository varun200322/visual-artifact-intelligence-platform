# System Architecture

## Project Name

Visual Artifact Intelligence Platform

## Architectural Principle

The system follows a reliability-first AI architecture:

SQL first. RAG second. LLM fallback last.

The goal is to avoid unnecessary LLM usage, reduce cost, improve reliability, and maintain explainability.

## High-Level Architecture

```text
User
 |
React Frontend
 |
Spring Boot Backend
 |
 |--- Artifact Upload
 |--- Metadata Management
 |--- AI Service Communication
 |--- Question Retrieval
 |--- RAG Routing
 |--- LLM Fallback
 |--- Feedback Logging
 |
PostgreSQL + pgvector
 |
FastAPI AI Service
 |
 |--- Artifact Classification
 |--- Embedding Support
 |--- LLM Integration
 |--- Evaluation Utilities