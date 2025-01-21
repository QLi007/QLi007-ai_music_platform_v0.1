package com.aimusic.backend.exception;

/**
 * 实体未找到异常
 * 
 * @author AI Music Team
 * @version 0.1.0
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 