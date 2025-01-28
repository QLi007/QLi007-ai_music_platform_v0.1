package com.aimusic.backend.domain.dto;

import com.aimusic.backend.domain.entity.MusicStatus;
import com.aimusic.backend.domain.entity.SunoMusic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Suno音乐DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SunoMusicDTO {

    private String id;
    private String generationId;
    private String prompt;
    private String style;
    private Integer duration;
    private MusicStatus status;
    private String audioUrl;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userId;

    /**
     * 从实体转换为DTO
     *
     * @param entity SunoMusic实体
     * @return SunoMusicDTO
     */
    public static SunoMusicDTO fromEntity(SunoMusic entity) {
        return SunoMusicDTO.builder()
                .id(entity.getId())
                .generationId(entity.getGenerationId())
                .prompt(entity.getPrompt())
                .style(entity.getStyle())
                .duration(entity.getDuration())
                .status(entity.getStatus())
                .audioUrl(entity.getAudioUrl())
                .errorMessage(entity.getErrorMessage())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .userId(entity.getUserId())
                .build();
    }

    /**
     * 转换为实体
     *
     * @return SunoMusic实体
     */
    public SunoMusic toEntity() {
        return SunoMusic.builder()
                .id(this.id)
                .generationId(this.generationId)
                .prompt(this.prompt)
                .style(this.style)
                .duration(this.duration)
                .status(this.status)
                .audioUrl(this.audioUrl)
                .errorMessage(this.errorMessage)
                .userId(this.userId)
                .build();
    }
} 