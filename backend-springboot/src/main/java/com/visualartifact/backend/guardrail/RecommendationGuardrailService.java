package com.visualartifact.backend.guardrail;

import com.visualartifact.backend.llm.GeneratedQuestionResponse;
import org.springframework.stereotype.Service;

@Service
public class RecommendationGuardrailService {

    private static final int MIN_QUESTION_LENGTH = 15;
    private static final int MAX_QUESTION_LENGTH = 250;

    private final PiiDetector piiDetector;

    public RecommendationGuardrailService(PiiDetector piiDetector) {
        this.piiDetector = piiDetector;
    }

    public GuardrailDecision validateGeneratedQuestion(
            GeneratedQuestionResponse question
    ) {
        if (question == null) {
            return GuardrailDecision.blocked("Generated question was null");
        }

        String questionText = question.questionText();

        if (questionText == null || questionText.isBlank()) {
            return GuardrailDecision.blocked("Generated question text was blank");
        }

        if (questionText.length() < MIN_QUESTION_LENGTH) {
            return GuardrailDecision.blocked("Generated question was too short");
        }

        if (questionText.length() > MAX_QUESTION_LENGTH) {
            return GuardrailDecision.blocked("Generated question was too long");
        }

        if (!questionText.trim().endsWith("?")) {
            return GuardrailDecision.needsReview("Generated output may not be a valid question");
        }

        if (piiDetector.containsPii(questionText)) {
            return GuardrailDecision.blocked("Generated question may contain PII");
        }

        if (containsUnsafeAssumption(questionText)) {
            return GuardrailDecision.needsReview("Generated question may assume unsupported visual facts");
        }

        return GuardrailDecision.passed();
    }

    private boolean containsUnsafeAssumption(String text) {
        String lower = text.toLowerCase();

        return lower.contains("student name")
                || lower.contains("phone number")
                || lower.contains("email address")
                || lower.contains("home address")
                || lower.contains("medical condition")
                || lower.contains("diagnosis");
    }
}