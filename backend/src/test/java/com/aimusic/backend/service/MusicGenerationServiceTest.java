package com.aimusic.backend.service;

import com.aimusic.backend.client.SunoApiClient;
import com.aimusic.backend.domain.dto.MusicDTO;
import com.aimusic.backend.domain.dto.MusicGenerationRequest;
import com.aimusic.backend.domain.entity.Music;
import com.aimusic.backend.domain.enums.MusicStatus;
import com.aimusic.backend.domain.entity.User;
import com.aimusic.backend.domain.repository.MusicRepository;
import com.aimusic.backend.domain.service.impl.MusicGenerationServiceImpl;
import com.aimusic.backend.exception.EntityNotFoundException;
import com.aimusic.backend.exception.MusicGenerationException;
import com.aimusic.backend.exception.ExternalApiException;
import com.aimusic.backend.utils.MusicTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 音乐生成服务测试类
 */
@ExtendWith(MockitoExtension.class)
class MusicGenerationServiceTest {

    @Mock
    private MusicRepository musicRepository;

    @Mock
    private SunoApiClient sunoApiClient;

    @InjectMocks
    private MusicGenerationServiceImpl musicGenerationService;

    private UUID musicId;
    private Music music;
    private String prompt;
    private User mockUser;

    @BeforeEach
    void setUp() {
        musicId = UUID.randomUUID();
        mockUser = MusicTestFactory.createTestUser();
        prompt = "测试提示词";
        
        music = Music.builder()
                .id(musicId)
                .user(mockUser)
                .prompt(prompt)
                .style("古典")
                .duration(60)
                .status(MusicStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void generateMusic_Success() throws Exception {
        // Given
        String generationId = "test-id";
        MusicGenerationRequest request = MusicGenerationRequest.builder()
                .prompt("test prompt")
                .style("classical")
                .duration(60)
                .userId(mockUser.getId())
                .build();
                
        when(sunoApiClient.generateMusic(anyString())).thenReturn(generationId);
        when(musicRepository.save(any(Music.class))).thenAnswer(invocation -> {
            Music savedMusic = invocation.getArgument(0);
            return savedMusic;
        });
        
        // When
        MusicDTO result = musicGenerationService.generateMusic(request).get();
        
        // Then
        assertNotNull(result);
        assertEquals(MusicStatus.PROCESSING, result.getStatus());
        assertEquals(generationId, result.getGenerationId());
        
        verify(musicRepository, times(2)).save(any(Music.class));
        verify(sunoApiClient).generateMusic(request.getPrompt());
    }

    @Test
    void generateMusic_ApiFailure() throws Exception {
        // Arrange
        MusicGenerationRequest request = MusicGenerationRequest.builder()
                .prompt("test prompt")
                .style("classical")
                .duration(60)
                .userId(mockUser.getId())
                .build();
                
        when(musicRepository.save(any(Music.class))).thenReturn(music);
        when(sunoApiClient.generateMusic(anyString()))
                .thenThrow(new ExternalApiException("API调用失败"));
        
        // Act & Assert
        MusicGenerationException exception = assertThrows(MusicGenerationException.class, () -> {
            musicGenerationService.generateMusic(request).get(5, TimeUnit.SECONDS);
        });
        
        assertEquals("生成音乐失败: API调用失败", exception.getMessage());
        verify(musicRepository, times(3)).save(any(Music.class));
        verify(sunoApiClient, times(2)).generateMusic(anyString());
    }

    @Test
    void getMusicStatus_Success() {
        // Arrange
        UUID musicId = UUID.randomUUID();
        String generationId = UUID.randomUUID().toString();
        
        Music music = Music.builder()
                .id(musicId)
                .generationId(generationId)
                .status(MusicStatus.PROCESSING)
                .build();
                
        when(musicRepository.findById(musicId)).thenReturn(Optional.of(music));
        
        // Act
        MusicDTO result = musicGenerationService.getMusicStatus(musicId.toString());
        
        // Assert
        assertNotNull(result);
        assertEquals(generationId, result.getGenerationId());
        assertEquals(MusicStatus.PROCESSING, result.getStatus());
        verify(musicRepository).findById(musicId);
    }

    @Test
    void getMusicStatus_NotFound() {
        // 设置mock行为
        when(musicRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // 执行测试并验证异常
        assertThrows(
                EntityNotFoundException.class,
                () -> musicGenerationService.getMusicStatus(UUID.randomUUID().toString())
        );
        
        // 验证交互
        verify(musicRepository).findById(any(UUID.class));
    }

    @Test
    void getMusicStatus_Failed() {
        // 准备测试数据
        music.setStatus(MusicStatus.FAILED);
        music.setErrorMessage("Generation failed");
        
        // 设置mock行为
        when(musicRepository.findById(musicId)).thenReturn(Optional.of(music));
        
        // 执行测试
        MusicDTO result = musicGenerationService.getMusicStatus(musicId.toString());
        
        // 验证结果
        assertNotNull(result);
        assertEquals(musicId, result.getId());
        assertEquals(music.getUserId(), result.getUserId());
        assertEquals(MusicStatus.FAILED, result.getStatus());
        assertEquals("Generation failed", result.getErrorMessage());
        
        // 验证交互
        verify(musicRepository).findById(musicId);
    }

    @Test
    void cancelGeneration_Success() {
        // 设置mock行为
        when(musicRepository.findById(musicId)).thenReturn(Optional.of(music));
        when(musicRepository.save(any(Music.class))).thenAnswer(invocation -> {
            Music savedMusic = invocation.getArgument(0);
            savedMusic.setStatus(MusicStatus.CANCELLED);
            return savedMusic;
        });

        // 执行测试
        MusicDTO result = musicGenerationService.cancelGeneration(musicId.toString());

        // 验证结果
        assertNotNull(result);
        assertEquals(musicId, result.getId());
        assertEquals(MusicStatus.CANCELLED, result.getStatus());
        
        // 验证交互
        verify(musicRepository).findById(musicId);
        verify(musicRepository).save(any(Music.class));
    }

    @Test
    void cancelGeneration_NotFound() {
        // 设置mock行为
        when(musicRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // 执行测试并验证异常
        assertThrows(
                EntityNotFoundException.class,
                () -> musicGenerationService.cancelGeneration(UUID.randomUUID().toString())
        );
        
        // 验证交互
        verify(musicRepository).findById(any(UUID.class));
    }

    @Test
    void getMusicStatus_InvalidGenerationId() {
        // Arrange
        String invalidId = "invalid-id";
        
        // Act & Assert
        MusicGenerationException exception = assertThrows(MusicGenerationException.class, () -> 
            musicGenerationService.getMusicStatus(invalidId)
        );
        
        assertEquals("无效的音乐ID格式", exception.getMessage());
        verify(musicRepository, never()).findById(any());
    }

    @Test
    void getMusicStatus_ApiError() {
        // Arrange
        UUID testId = UUID.randomUUID();
        when(musicRepository.findById(testId))
                .thenReturn(Optional.of(music));
        
        // Act & Assert
        MusicDTO result = musicGenerationService.getMusicStatus(testId.toString());
        
        assertNotNull(result);
        assertEquals(music.getId(), result.getId());
        verify(musicRepository).findById(testId);
    }

    @Test
    void getMusicStatus_InvalidStatusResponse() {
        // Arrange
        UUID testId = UUID.randomUUID();
        Music invalidMusic = Music.builder()
                .id(testId)
                .status(null)
                .build();
                
        when(musicRepository.findById(testId))
                .thenReturn(Optional.of(invalidMusic));
        
        // Act & Assert
        MusicGenerationException exception = assertThrows(MusicGenerationException.class, () ->
            musicGenerationService.getMusicStatus(testId.toString())
        );
        
        assertEquals("无效的音乐状态: null", exception.getMessage());
        verify(musicRepository).findById(testId);
    }

    @Test
    void generateMusic_ConcurrentRequests() throws Exception {
        // Given
        int numRequests = 3;
        List<CompletableFuture<MusicDTO>> futures = new ArrayList<>();
        String generationId = "test-id";
        
        when(sunoApiClient.generateMusic(anyString())).thenReturn(generationId);
        when(musicRepository.save(any(Music.class))).thenAnswer(invocation -> {
            Music savedMusic = invocation.getArgument(0);
            return savedMusic;
        });
        
        // When
        for (int i = 0; i < numRequests; i++) {
            MusicGenerationRequest request = MusicGenerationRequest.builder()
                    .prompt("test prompt")
                    .style("classical")
                    .duration(60)
                    .userId(mockUser.getId())
                    .build();
            futures.add(musicGenerationService.generateMusic(request));
        }
        
        // Then
        for (CompletableFuture<MusicDTO> future : futures) {
            MusicDTO result = future.get();
            assertNotNull(result);
            assertEquals(MusicStatus.PROCESSING, result.getStatus());
            assertEquals(generationId, result.getGenerationId());
        }
        
        verify(sunoApiClient, times(numRequests)).generateMusic(anyString());
        verify(musicRepository, times(numRequests * 2)).save(any(Music.class));
    }

    @Test
    void generateMusic_RetryOnFailure() throws Exception {
        // Arrange
        MusicGenerationRequest request = MusicGenerationRequest.builder()
                .prompt("test prompt")
                .style("classical")
                .duration(60)
                .userId(mockUser.getId())
                .build();
                
        when(musicRepository.save(any(Music.class))).thenReturn(music);
        when(sunoApiClient.generateMusic(anyString()))
                .thenThrow(new ExternalApiException("First attempt failed"))
                .thenThrow(new ExternalApiException("Second attempt failed"));
        
        // Act & Assert
        MusicGenerationException exception = assertThrows(MusicGenerationException.class, () -> {
            musicGenerationService.generateMusic(request).get(5, TimeUnit.SECONDS);
        });
        
        assertEquals("生成音乐失败: Second attempt failed", exception.getMessage());
        verify(musicRepository, times(3)).save(any(Music.class));
        verify(sunoApiClient, times(2)).generateMusic(anyString());
    }

    @Test
    void generateMusic_TransactionRollback() {
        // Arrange
        MusicGenerationRequest request = MusicGenerationRequest.builder()
                .prompt("test prompt")
                .style("classical")
                .duration(60)
                .userId(mockUser.getId())
                .build();
                
        when(musicRepository.save(any(Music.class)))
                .thenThrow(new RuntimeException("Database error"));
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            musicGenerationService.generateMusic(request).get(5, TimeUnit.SECONDS);
        });
        
        verify(musicRepository).save(any(Music.class));
        verify(sunoApiClient, never()).generateMusic(anyString());
    }
} 