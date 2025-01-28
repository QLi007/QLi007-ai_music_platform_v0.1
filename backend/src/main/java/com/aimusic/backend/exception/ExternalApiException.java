package com.aimusic.backend.exception;

/**
 * 外部API调用异常
 */
public class ExternalApiException extends RuntimeException {
    
    public ExternalApiException(String message) {
        super(message);
    }
    
    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
} 