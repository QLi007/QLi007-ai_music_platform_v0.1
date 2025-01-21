package com.aimusic.backend.service;

import com.aimusic.backend.domain.dto.MusicDTO;
import com.aimusic.backend.domain.dto.MusicGenerationRequest;
import com.aimusic.backend.domain.entity.Music;
import com.aimusic.backend.domain.entity.MusicStatus;
import com.aimusic.backend.domain.repository.MusicRepository;
import com.aimusic.backend.domain.service.impl.MusicServiceImpl;
import com.aimusic.backend.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 音乐服务测试类
 */
@ExtendWith(MockitoExtension.class)
class MusicServiceTest {

    @Mock
    private MusicRepository musicRepository;

    @InjectMocks
    private MusicServiceImpl musicService;

    private Music mockMusic;
    private MusicGenerationRequest mockRequest;
    private UUID mockId;

    @BeforeEach
    void setUp() {
        mockId = UUID.randomUUID();
        
        // 初始化测试用的Music实体
        mockMusic = new Music();
        mockMusic.setId(mockId);
        mockMusic.setPrompt("测试提示词");
        mockMusic.setStyle("古典");
        mockMusic.setDuration(60);
        mockMusic.setStatus(MusicStatus.PENDING);
        mockMusic.setCreatedAt(LocalDateTime.now());
        mockMusic.setUpdatedAt(LocalDateTime.now());

        // 初始化测试用的请求对象
        mockRequest = new MusicGenerationRequest();
        mockRequest.setPrompt("测试提示词");
        mockRequest.setStyle("古典");
        mockRequest.setDuration(60);
    }

    @Test
    void generateMusic_ShouldCreateNewMusic() {
        // Given
        when(musicRepository.save(any(Music.class))).thenReturn(mockMusic);

        // When
        MusicDTO result = musicService.generateMusic(mockRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPrompt()).isEqualTo(mockRequest.getPrompt());
        assertThat(result.getStyle()).isEqualTo(mockRequest.getStyle());
        assertThat(result.getDuration()).isEqualTo(mockRequest.getDuration());
        assertThat(result.getStatus()).isEqualTo(MusicStatus.PENDING);
        verify(musicRepository).save(any(Music.class));
    }

    @Test
    void getMusicById_ShouldReturnMusic_WhenMusicExists() {
        // Given
        when(musicRepository.findById(mockId)).thenReturn(Optional.of(mockMusic));

        // When
        MusicDTO result = musicService.getMusicById(mockId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(mockId);
        verify(musicRepository).findById(mockId);
    }

    @Test
    void getMusicById_ShouldThrowException_WhenMusicNotExists() {
        // Given
        when(musicRepository.findById(mockId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> musicService.getMusicById(mockId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("音乐不存在");
    }

    @Test
    void listMusic_ShouldReturnPageOfMusic() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Music> mockPage = new PageImpl<>(Collections.singletonList(mockMusic));
        when(musicRepository.findAllByOrderByCreatedAtDesc(pageable)).thenReturn(mockPage);

        // When
        Page<MusicDTO> result = musicService.listMusic(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(mockId);
        verify(musicRepository).findAllByOrderByCreatedAtDesc(pageable);
    }

    @Test
    void updateMusicStatus_ShouldUpdateStatus_WhenMusicExists() {
        // Given
        String url = "http://example.com/music.mp3";
        when(musicRepository.findById(mockId)).thenReturn(Optional.of(mockMusic));
        when(musicRepository.save(any(Music.class))).thenReturn(mockMusic);

        // When
        musicService.updateMusicStatus(mockId, url);

        // Then
        verify(musicRepository).findById(mockId);
        verify(musicRepository).save(any(Music.class));
        assertThat(mockMusic.getStatus()).isEqualTo(MusicStatus.COMPLETED);
        assertThat(mockMusic.getUrl()).isEqualTo(url);
    }

    @Test
    void updateMusicStatus_ShouldThrowException_WhenMusicNotExists() {
        // Given
        String url = "http://example.com/music.mp3";
        when(musicRepository.findById(mockId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> musicService.updateMusicStatus(mockId, url))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("音乐不存在");
    }
} 