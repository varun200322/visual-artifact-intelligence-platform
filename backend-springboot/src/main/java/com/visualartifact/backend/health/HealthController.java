package com.visualartifact.backend.health;

import com.visualartifact.backend.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("api/v1/health")
    public ResponseEntity<ApiResponse<HealthResponse>> health() {
        HealthResponse response = new HealthResponse(
                "backend-springboot",
                "UP",
                "0.0.1-SNAPSHOT"
        );
        return ResponseEntity.ok(ApiResponse.success("Backend service is running", response));
    }
}
