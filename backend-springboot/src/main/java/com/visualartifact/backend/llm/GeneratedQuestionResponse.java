package com.visualartifact.backend.llm;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GeneratedQuestionResponse(
        @JsonProperty("question_text")
        String questionText,

        String difficulty,

        @JsonProperty("skill_tag")
        String skillTag,

        @JsonProperty("topic_tag")
        String topicTag
) {
}
