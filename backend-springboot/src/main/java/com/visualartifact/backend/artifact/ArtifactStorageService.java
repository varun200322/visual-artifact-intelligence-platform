package com.visualartifact.backend.artifact;

import com.visualartifact.backend.common.AppException;
import com.visualartifact.backend.common.ErrorCode;
import com.visualartifact.backend.config.StorageProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ArtifactStorageService {

    private final StorageProperties storageProperties;

    public ArtifactStorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    public StoredArtifact store(MultipartFile file, String detectedContentType) {
        try {
            Path storageDirectory = Path.of(storageProperties.localPath()).toAbsolutePath().normalize();
            Files.createDirectories(storageDirectory);

            String extension = extensionForContentType(detectedContentType);
            String storedFileName = UUID.randomUUID() + extension;
            Path targetPath = storageDirectory.resolve(storedFileName).normalize();

            if (!targetPath.startsWith(storageDirectory)) {
                throw new AppException(
                        ErrorCode.FILE_STORAGE_FAILED,
                        "Invalid storage path"
                );
            }

            Files.copy(file.getInputStream(), targetPath);

            return new StoredArtifact(
                    storedFileName,
                    targetPath.toString()
            );
        } catch (IOException exception) {
            throw new AppException(
                    ErrorCode.FILE_STORAGE_FAILED,
                    "Could not store uploaded artifact"
            );
        }
    }

    private String extensionForContentType(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            case "image/webp" -> ".webp";
            default -> throw new AppException(
                    ErrorCode.UNSUPPORTED_FILE_TYPE,
                    "Unsupported image content type"
            );
        };
    }

    public record StoredArtifact(
            String storedFileName,
            String storagePath
    ) {
    }
}