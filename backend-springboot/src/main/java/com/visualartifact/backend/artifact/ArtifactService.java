package com.visualartifact.backend.artifact;

import com.visualartifact.backend.classification.AiClassificationResponse;
import com.visualartifact.backend.classification.ArtifactClassification;
import com.visualartifact.backend.classification.ArtifactClassificationRepository;
import com.visualartifact.backend.classification.ArtifactClassificationResponse;
import com.visualartifact.backend.classification.FastApiClassificationClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Service
public class ArtifactService {

    private final ArtifactFileValidator artifactFileValidator;
    private final ArtifactStorageService artifactStorageService;
    private final ArtifactRepository artifactRepository;
    private final FastApiClassificationClient fastApiClassificationClient;
    private final ArtifactClassificationRepository artifactClassificationRepository;

    public ArtifactService(
            ArtifactFileValidator artifactFileValidator,
            ArtifactStorageService artifactStorageService,
            ArtifactRepository artifactRepository,
            FastApiClassificationClient fastApiClassificationClient,
            ArtifactClassificationRepository artifactClassificationRepository
    ) {
        this.artifactFileValidator = artifactFileValidator;
        this.artifactStorageService = artifactStorageService;
        this.artifactRepository = artifactRepository;
        this.fastApiClassificationClient = fastApiClassificationClient;
        this.artifactClassificationRepository = artifactClassificationRepository;
    }

    public ArtifactUploadResponse uploadArtifact(MultipartFile file) {
        String detectedContentType = artifactFileValidator.validateAndDetectContentType(file);

        ArtifactStorageService.StoredArtifact storedArtifact =
                artifactStorageService.store(file, detectedContentType);

        Artifact artifact = new Artifact(
                cleanOriginalFileName(file.getOriginalFilename()),
                storedArtifact.storedFileName(),
                detectedContentType,
                file.getSize(),
                storedArtifact.storagePath(),
                ArtifactUploadStatus.STORED
        );

        Artifact savedArtifact = artifactRepository.save(artifact);

        AiClassificationResponse aiResponse =
                fastApiClassificationClient.classify(Path.of(storedArtifact.storagePath()));

        ArtifactClassification classification = new ArtifactClassification(
                savedArtifact,
                aiResponse.artifactType(),
                aiResponse.confidence(),
                aiResponse.reasoningSummary(),
                aiResponse.modelUsed(),
                aiResponse.latencyMs()
        );

        ArtifactClassification savedClassification =
                artifactClassificationRepository.save(classification);

        savedArtifact.markClassified();
        Artifact classifiedArtifact = artifactRepository.save(savedArtifact);

        ArtifactClassificationResponse classificationResponse =
                new ArtifactClassificationResponse(
                        savedClassification.getId(),
                        savedClassification.getArtifactType(),
                        savedClassification.getConfidence(),
                        savedClassification.getReasoningSummary(),
                        savedClassification.getModelUsed(),
                        savedClassification.getLatencyMs(),
                        savedClassification.getCreatedAt()
                );

        return new ArtifactUploadResponse(
                classifiedArtifact.getId(),
                classifiedArtifact.getOriginalFileName(),
                classifiedArtifact.getFileType(),
                classifiedArtifact.getFileSizeBytes(),
                classifiedArtifact.getUploadStatus().name(),
                classifiedArtifact.getCreatedAt(),
                classificationResponse
        );
    }

    private String cleanOriginalFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank()) {
            return "unknown";
        }
        return originalFileName
                .replace("\\", "")
                .replace("/", "")
                .trim();
    }
}