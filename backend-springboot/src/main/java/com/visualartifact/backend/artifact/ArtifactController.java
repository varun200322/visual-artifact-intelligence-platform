package com.visualartifact.backend.artifact;

import com.visualartifact.backend.common.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/artifacts")
public class ArtifactController {

    private final ArtifactService artifactService;

    public ArtifactController(ArtifactService artifactService) {
        this.artifactService = artifactService;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<ArtifactUploadResponse>> uploadArtifact(
            @RequestParam("file") MultipartFile file
    ) {
        ArtifactUploadResponse response = artifactService.uploadArtifact(file);

        return ResponseEntity.ok(
                ApiResponse.success("Artifact uploaded successfully", response)
        );
    }
}