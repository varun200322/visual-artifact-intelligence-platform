package com.visualartifact.backend.artifact;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArtifactService {

    private final ArtifactFileValidator artifactFileValidator;
    private final ArtifactStorageService artifactStorageService;
    private final ArtifactRepository artifactRepository;

    public ArtifactService(
            ArtifactFileValidator artifactFileValidator,
            ArtifactStorageService artifactStorageService,
            ArtifactRepository artifactRepository
    ) {
        this.artifactFileValidator = artifactFileValidator;
        this.artifactStorageService = artifactStorageService;
        this.artifactRepository = artifactRepository;
    }

    @Transactional
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

        return new ArtifactUploadResponse(
                savedArtifact.getId(),
                savedArtifact.getOriginalFileName(),
                savedArtifact.getFileType(),
                savedArtifact.getFileSizeBytes(),
                savedArtifact.getUploadStatus().name(),
                savedArtifact.getCreatedAt()
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