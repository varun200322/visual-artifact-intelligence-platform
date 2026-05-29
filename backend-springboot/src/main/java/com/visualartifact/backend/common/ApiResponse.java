package com.visualartifact.backend.common;
import java.time.Instant;

public record ApiResponse<T>(boolean success, String message, T data,
                             String errorCode, Instant timestamp)
{
    public static<T> ApiResponse<T> success(String message, T data)
    {
        return new ApiResponse<>(true, message, data, null, Instant.now());
    }
    public static <T> ApiResponse<T> failure(String message, String errorCode) {
        return new ApiResponse<>(
                false,
                message,
                null,
                errorCode,
                Instant.now()
        );
    }
}

