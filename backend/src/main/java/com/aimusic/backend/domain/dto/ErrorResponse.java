package com.aimusic.backend.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 统一错误响应
 * 用于向客户端返回标准格式的错误信息
 *
 * @author AI Music Team
 * @version 0.1.0
 */
@Data
@Builder
public class ErrorResponse {
    /**
     * 错误状态码
     */
    private int status;

    /**
     * 错误代码
     */
    private String code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 详细错误信息
     */
    private Map<String, String> details;

    /**
     * 错误发生时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 错误堆栈（仅在开发环境显示）
     */
    private List<String> trace;
} 