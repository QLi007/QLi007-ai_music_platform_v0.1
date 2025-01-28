package com.aimusic.backend.utils;

import com.aimusic.backend.domain.entity.Music;
import com.aimusic.backend.domain.entity.User;
import com.aimusic.backend.domain.enums.MusicStatus;
import com.aimusic.backend.domain.dto.MusicGenerationRequest;
import com.aimusic.backend.domain.dto.MusicResponse;
import com.aimusic.backend.domain.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试数据工厂类
 * 用于生成测试所需的各种数据对象
 */
public class TestDataFactory {
    
    public static User createTestUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("testUser" + System.currentTimeMillis())
                .email("test" + System.currentTimeMillis() + "@example.com")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static UserDTO createTestUserDTO() {
        User user = createTestUser();
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static Music createTestMusic() {
        return createTestMusic(MusicStatus.COMPLETED);
    }
    
    public static Music createTestMusic(MusicStatus status) {
        return Music.builder()
                .id(UUID.randomUUID())
                .userId(createTestUser().getId())
                .prompt("Test prompt for AI music generation")
                .generationId("test-generation-id")
                .audioUrl(status == MusicStatus.COMPLETED ? "https://example.com/test.mp3" : null)
                .lyrics(status == MusicStatus.COMPLETED ? "Test lyrics" : null)
                .duration(180)
                .style("Classical")
                .status(status)
                .errorMessage(status == MusicStatus.FAILED ? "Generation failed" : null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static MusicGenerationRequest createTestMusicGenerationRequest() {
        return MusicGenerationRequest.builder()
                .prompt("Test prompt for AI music generation")
                .style("classical")
                .duration(60)
                .userId(createTestUser().getId())
                .build();
    }

    public static MusicResponse createTestMusicResponse() {
        return MusicResponse.builder()
                .id(UUID.randomUUID())
                .title("Test Music Response")
                .url("http://example.com/test.mp3")
                .duration(60)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static List<Music> createTestMusicList() {
        return Arrays.stream(MusicStatus.values())
                .map(TestDataFactory::createTestMusic)
                .collect(Collectors.toList());
    }
} 