package com.visualartifact.backend.question;

import com.visualartifact.backend.common.ApiResponse;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuestionAdminController {
    private final QuestionEmbeddingBackfillService questionEmbeddingBackfillService;

    public QuestionAdminController(QuestionEmbeddingBackfillService questionEmbeddingBackfillService) {
        this.questionEmbeddingBackfillService = questionEmbeddingBackfillService;
    }

    @PostMapping("/api/v1/admin/questions/backfill-embeddings")
    public ResponseEntity<ApiResponse<Integer>> backfillMissingEmbeddings() {
        int updatedCount = questionEmbeddingBackfillService.backfillMissingEmbeddings();

        return ResponseEntity.ok(
                ApiResponse.success("Question embeddings backfilled", updatedCount)
        );
    }

    //START STEP16
}
