package com.aimusic.backend.utils;

import com.aimusic.backend.domain.entity.Music;
import com.aimusic.backend.domain.dto.MusicDTO;
import com.aimusic.backend.domain.dto.MusicGenerationRequest;
import com.aimusic.backend.domain.entity.MusicStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 音乐测试数据工厂类
 * 用于创建测试用的音乐实体和DTO对象
 */
public class MusicTestFactory {
    
    /**
     * 创建测试用音乐实体
     */
    public static Music createMusic() {
        return Music.builder()
                .id(UUID.randomUUID())
                .title("Test Music")
                .prompt("Test prompt for AI music generation")
                .style("Classical")
                .duration(180)
                .status(MusicStatus.COMPLETED)
                .url("https://example.com/music.mp3")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 创建测试用音乐DTO
     */
    public static MusicDTO createMusicDTO() {
        return MusicDTO.builder()
                .id(UUID.randomUUID())
                .title("Test Music DTO")
                .prompt("Test prompt for AI music generation")
                .style("Classical")
                .duration(180)
                .status(MusicStatus.COMPLETED)
                .url("https://example.com/music.mp3")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 创建测试用音乐生成请求
     */
    public static MusicGenerationRequest createMusicGenerationRequest() {
        return MusicGenerationRequest.builder()
                .title("Test Music Request")
                .prompt("Test prompt for music generation")
                .style("Classical")
                .duration(180)
                .build();
    }
} 