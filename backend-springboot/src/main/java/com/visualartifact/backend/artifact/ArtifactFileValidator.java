package com.visualartifact.backend.artifact;

import com.visualartifact.backend.common.AppException;
import com.visualartifact.backend.common.ErrorCode;
import com.visualartifact.backend.config.UploadProperties;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class ArtifactFileValidator {
    private final UploadProperties uploadProperties;
    private final Tika tika;

    public ArtifactFileValidator(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
        this.tika = new Tika();
    }

    public String validateAndDetectContentType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_FILE, "Uploaded file must not be empty");
        }

        if (file.getSize() > uploadProperties.maxFileSizeBytes()) {
            throw new AppException(
                    ErrorCode.FILE_TOO_LARGE,
                    "Uploaded file exceeds the maximum allowed size"
            );
        }

        String detectedContentType = detectContentType(file);

        if (!uploadProperties.allowedContentTypes().contains(detectedContentType)) {
            throw new AppException(
                    ErrorCode.UNSUPPORTED_FILE_TYPE,
                    "Only PNG, JPEG, and WebP image files are supported"
            );
        }

        return detectedContentType;
    }

    private String detectContentType(MultipartFile file) {
        try {
            return tika.detect(file.getInputStream());
        } catch (IOException exception) {
            throw new AppException(
                    ErrorCode.INVALID_FILE,
                    "Could not inspect uploaded file"
            );
        }
    }
}
