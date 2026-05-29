package com.visualartifact.backend.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    VALIDATION_ERROR("COMMON_400",
            HttpStatus.BAD_REQUEST,
            "Request Validation Failed"),
    RESOURCE_NOT_FOUND("COMMON_404",
            HttpStatus.NOT_FOUND,
            "Request resource was not found"),
    INTERNAL_SERVER_ERROR("COMMON_500",
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected server error occurred");
    private final String code;
    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(String code, HttpStatus status, String defaultMessage) {
        this.code = code;
        this.status = status;
        this.defaultMessage = defaultMessage;
    }
    public String getCode() {
        return code;
    }
    public HttpStatus getStatus() {
        return status;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}