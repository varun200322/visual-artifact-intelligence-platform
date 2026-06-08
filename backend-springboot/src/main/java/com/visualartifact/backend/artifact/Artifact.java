package com.visualartifact.backend.artifact;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="artifacts")
@Getter
@Setter
public class Artifact {
    @Id
    @Column(nullable=false, updatable=false)
    private UUID id;

    @Column(name="original_file_name", nullable=false, length=250)
    private String originalFileName;

    @Column(name="stored_file_name", nullable=false, length=250)
    private String storedFileName;

    @Column(name="file_type", nullable=false)
    private String fileType;

    @Column(name="file_size_bytes", nullable=false)
    private Long fileSizeBytes;

    @Column(name = "storage_path", nullable = false, length = 500)
    private String storagePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_status", nullable = false, length = 50)
    private ArtifactUploadStatus uploadStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Artifact() {}

    public Artifact(
            String originalFileName,
            String storedFileName,
            String fileType,
            Long fileSizeBytes,
            String storagePath,
            ArtifactUploadStatus uploadStatus
    ) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fileType = fileType;
        this.fileSizeBytes = fileSizeBytes;
        this.storagePath = storagePath;
        this.uploadStatus = uploadStatus;
    }

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
        if(createdAt==null) {
            createdAt = now;
        }
        updatedAt=now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
