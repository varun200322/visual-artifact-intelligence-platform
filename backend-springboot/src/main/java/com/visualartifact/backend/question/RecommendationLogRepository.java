package com.visualartifact.backend.question;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecommendationLogRepository extends JpaRepository<RecommendationLog, UUID> {
}
