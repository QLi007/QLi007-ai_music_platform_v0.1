package com.aimusic.backend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 音乐响应DTO
 * 用于返回音乐生成结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicResponse {
    private UUID id;
    private String title;
    private String url;
    private Integer duration;
    private LocalDateTime createdAt;
} 