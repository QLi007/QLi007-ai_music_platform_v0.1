package com.aimusic.backend.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 错误响应DTO
 */
@Data
@NoArgsConstructor
public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
} 