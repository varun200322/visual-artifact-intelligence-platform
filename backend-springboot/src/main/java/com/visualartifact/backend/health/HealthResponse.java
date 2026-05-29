package com.visualartifact.backend.health;

public record HealthResponse(String service, String status, String version) {
}
