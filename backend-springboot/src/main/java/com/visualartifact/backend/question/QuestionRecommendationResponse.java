package com.visualartifact.backend.question;

import java.util.UUID;

public record QuestionRecommendationResponse(
        UUID questionId,
        String questionText,
        String difficulty,
        String skillTag,
        String topicTag,
        Integer gradeLevel,
        String source,
        Integer rank
) {
}
