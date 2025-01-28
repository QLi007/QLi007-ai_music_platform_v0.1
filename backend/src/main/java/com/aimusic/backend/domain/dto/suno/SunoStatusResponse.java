package com.aimusic.backend.domain.dto.suno;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Suno API状态响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SunoStatusResponse {
    /**
     * 生成状态
     */
    private String status;

    /**
     * 音频URL（如果生成完成）
     */
    private String audioUrl;

    /**
     * 错误信息（如果生成失败）
     */
    private String errorMessage;

    /**
     * 生成进度（0-100）
     */
    private Integer progress;

    /**
     * 预计剩余时间（秒）
     */
    private Integer estimatedTime;
} 