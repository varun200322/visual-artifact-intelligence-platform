package com.visualartifact.backend.guardrail;

public record GuardrailDecision(
        boolean allowed,
        boolean humanReviewRequired,
        String status,
        String reason
) {
    public static GuardrailDecision passed() {
        return new GuardrailDecision(
                true,
                false,
                "PASSED",
                "Output passed guardrail checks"
        );
    }

    public static GuardrailDecision needsReview(String reason) {
        return new GuardrailDecision(
                true,
                true,
                "REVIEW_REQUIRED",
                reason
        );
    }

    public static GuardrailDecision blocked(String reason) {
        return new GuardrailDecision(
                false,
                true,
                "BLOCKED",
                reason
        );
    }
}