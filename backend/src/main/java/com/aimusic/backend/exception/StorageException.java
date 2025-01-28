package com.aimusic.backend.exception;

/**
 * 存储异常类
 * 处理文件存储过程中的异常
 */
public class StorageException extends RuntimeException {

    /**
     * 构造函数
     *
     * @param message 异常信息
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * 构造函数
     *
     * @param message 异常信息
     * @param cause 异常原因
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
} 