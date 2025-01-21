package com.aimusic.backend.domain.dto;

import com.aimusic.backend.domain.entity.MusicStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 音乐DTO
 * 
 * @author AI Music Team
 * @version 0.1.0
 */
@Data
public class MusicDTO {
    /**
     * 音乐ID
     */
    private UUID id;

    /**
     * 提示词
     */
    private String prompt;

    /**
     * 音乐风格
     */
    private String style;

    /**
     * 音乐时长（秒）
     */
    private Integer duration;

    /**
     * 音乐状态
     */
    private MusicStatus status;

    /**
     * 音乐URL
     */
    private String url;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 