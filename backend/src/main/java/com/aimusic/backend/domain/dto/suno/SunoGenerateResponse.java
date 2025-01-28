package com.aimusic.backend.domain.dto.suno;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Suno API生成响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SunoGenerateResponse {
    /**
     * 生成任务ID
     */
    private String generationId;

    /**
     * 音频URL（如果waitAudio为true且生成成功）
     */
    private String audioUrl;

    /**
     * 生成状态
     */
    private String status;

    /**
     * 错误信息（如果生成失败）
     */
    private String errorMessage;
} 