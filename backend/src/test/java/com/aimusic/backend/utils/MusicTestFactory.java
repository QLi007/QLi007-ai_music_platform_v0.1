package com.aimusic.backend.utils;

import com.aimusic.backend.domain.dto.MusicDTO;
import com.aimusic.backend.domain.dto.MusicGenerationRequest;
import com.aimusic.backend.domain.entity.Music;
import com.aimusic.backend.domain.enums.MusicStatus;
import com.aimusic.backend.domain.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 音乐测试数据工厂类
 */
public class MusicTestFactory {

    /**
     * 创建测试用户
     *
     * @return User 实体
     */
    public static User createTestUser() {
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        return User.builder()
                .id(UUID.randomUUID())
                .username("test-user-" + randomSuffix)
                .email("test" + randomSuffix + "@example.com")
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 创建测试音乐实体
     *
     * @param user 关联的用户
     * @return Music 实体
     */
    public static Music createTestMusic(User user) {
        return Music.builder()
                .id(UUID.randomUUID())
                .user(user)
                .prompt("测试音乐生成")
                .status(MusicStatus.PENDING)
                .audioUrl("http://example.com/test.mp3")
                .lyrics("测试歌词")
                .duration(180)
                .generationId("gen_" + UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 创建测试音乐DTO
     *
     * @param userId 用户ID
     * @return MusicDTO
     */
    public static MusicDTO createTestMusicDTO(UUID userId) {
        return MusicDTO.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .prompt("测试音乐生成")
                .status(MusicStatus.PENDING)
                .audioUrl("http://example.com/test.mp3")
                .lyrics("测试歌词")
                .duration(180)
                .generationId("gen_" + UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 创建测试音乐生成请求
     *
     * @param userId 用户ID
     * @return MusicGenerationRequest
     */
    public static MusicGenerationRequest createMusicGenerationRequest(UUID userId) {
        return MusicGenerationRequest.builder()
                .prompt("测试音乐生成")
                .style("流行")
                .userId(userId)
                .generateLyrics(true)
                .build();
    }
} 