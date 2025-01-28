package com.aimusic.backend.domain.dto.suno;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Suno API音乐生成请求DTO
 * 用于封装音乐生成的请求参数
 *
 * @author AI Music Team
 * @version 1.0
 * @since 2024-03-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SunoGenerateRequest {
    
    /**
     * 音乐生成提示词
     */
    @NotBlank(message = "提示词不能为空")
    @Size(min = 10, max = 1000, message = "提示词长度必须在10-1000字符之间")
    private String prompt;
    
    /**
     * 音乐时长（秒）
     */
    @Builder.Default
    private Integer duration = 30;
    
    /**
     * 音乐风格
     */
    private String style;
    
    /**
     * 是否生成歌词
     */
    @Builder.Default
    private boolean generateLyrics = false;
    
    /**
     * 是否使用AI声音
     */
    @Builder.Default
    private boolean useAiVoice = false;

    /**
     * 是否等待音频生成完成
     */
    @Builder.Default
    private boolean waitAudio = false;
} 