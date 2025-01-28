package com.aimusic.backend.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 音乐生成请求DTO
 * 用于接收前端的音乐生成请求参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicGenerationRequest {
    
    /**
     * 提示词
     */
    @NotBlank(message = "提示词不能为空")
    @Size(min = 1, max = 1000, message = "提示词长度必须在1-1000之间")
    private String prompt;
    
    /**
     * 音乐风格
     */
    @NotBlank(message = "音乐风格不能为空")
    private String style;
    
    /**
     * 音乐时长(秒)
     */
    @NotNull(message = "音乐时长不能为空")
    private Integer duration;
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private UUID userId;
    
    /**
     * 生成任务ID
     */
    private String generationId;
    
    /**
     * 是否等待音频生成完成
     */
    @Builder.Default
    private boolean waitAudio = false;
    
    /**
     * 是否生成歌词
     */
    @Builder.Default
    private boolean generateLyrics = true;
    
    /**
     * 是否使用AI声音
     */
    @Builder.Default
    private boolean useAiVoice = false;
    
    /**
     * 音乐标题
     */
    @NotBlank(message = "音乐标题不能为空")
    private String title;
    
    /**
     * 音乐描述
     */
    private String description;
    
    /**
     * 生成参数
     */
    private GenerationParams params;
    
    /**
     * 音乐生成参数
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerationParams {
        /**
         * 温度参数(0.0-1.0)
         * 控制生成音乐的随机性
         */
        @Builder.Default
        private Double temperature = 0.8;
        
        /**
         * 节奏类型
         */
        private String rhythm;
        
        /**
         * 情感类型
         */
        private String emotion;
        
        /**
         * 乐器类型
         */
        private String instrument;
    }
} 