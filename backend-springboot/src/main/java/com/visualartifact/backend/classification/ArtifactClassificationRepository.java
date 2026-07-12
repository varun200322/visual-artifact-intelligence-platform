package com.visualartifact.backend.classification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtifactClassificationRepository extends JpaRepository<ArtifactClassification, UUID> {

}
