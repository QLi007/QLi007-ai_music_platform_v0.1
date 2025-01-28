package com.aimusic.backend.domain.dto;

import com.aimusic.backend.domain.entity.Music;
import com.aimusic.backend.domain.enums.MusicStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 音乐DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicDTO {
    
    /**
     * ID
     */
    private UUID id;

    /**
     * 用户ID
     */
    private UUID userId;

    /**
     * 提示词
     */
    private String prompt;

    /**
     * 生成任务ID
     */
    private String generationId;

    /**
     * 音频URL
     */
    private String audioUrl;

    /**
     * 歌词
     */
    private String lyrics;

    /**
     * 音乐时长（秒）
     */
    private Integer duration;

    /**
     * 音乐风格
     */
    private String style;

    /**
     * 生成状态
     */
    private MusicStatus status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 从实体创建DTO
     *
     * @param music 音乐实体
     * @return 音乐DTO
     */
    public static MusicDTO fromEntity(Music music) {
        return MusicDTO.builder()
                .id(music.getId())
                .userId(music.getUserId())
                .prompt(music.getPrompt())
                .style(music.getStyle())
                .status(music.getStatus())
                .audioUrl(music.getAudioUrl())
                .lyrics(music.getLyrics())
                .duration(music.getDuration())
                .errorMessage(music.getErrorMessage())
                .generationId(music.getGenerationId())
                .createdAt(music.getCreatedAt())
                .updatedAt(music.getUpdatedAt())
                .build();
    }
} 