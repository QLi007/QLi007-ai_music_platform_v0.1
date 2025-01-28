package com.aimusic.backend.exception;

import com.aimusic.backend.domain.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 处理应用中所有抛出的异常，将其转换为标准的错误响应格式
 *
 * @author AI Music Team
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理实体未找到异常
     *
     * @param e 实体未找到异常
     * @return 错误响应
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("实体未找到", e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    /**
     * 处理音乐生成异常
     *
     * @param e 音乐生成异常
     * @return 错误响应
     */
    @ExceptionHandler(MusicGenerationException.class)
    public ResponseEntity<ErrorResponse> handleMusicGenerationException(MusicGenerationException e) {
        log.error("音乐生成失败", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage()));
    }

    /**
     * 处理外部API调用异常
     *
     * @param e 外部API调用异常
     * @return 错误响应
     */
    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApiException(ExternalApiException e) {
        log.error("外部API调用失败", e);
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse(e.getMessage()));
    }

    /**
     * 处理存储操作异常
     *
     * @param e 存储操作异常
     * @return 错误响应
     */
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponse> handleStorageException(StorageException e) {
        log.error("存储操作失败", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage()));
    }

    /**
     * 处理存储文件未找到异常
     *
     * @param e 存储文件未找到异常
     * @return 错误响应
     */
    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStorageFileNotFoundException(StorageFileNotFoundException e) {
        log.error("存储文件未找到", e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    /**
     * 处理未知异常
     *
     * @param e 未知异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("未知错误", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("服务器内部错误"));
    }
} 