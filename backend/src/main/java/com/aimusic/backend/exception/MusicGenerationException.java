package com.aimusic.backend.exception;

/**
 * 音乐生成异常
 * 处理音乐生成过程中的异常情况
 *
 * @author AI Music Team
 * @version 1.0
 * @since 2024-03-21
 */
public class MusicGenerationException extends RuntimeException {
    
    public MusicGenerationException(String message) {
        super(message);
    }
    
    public MusicGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
} 