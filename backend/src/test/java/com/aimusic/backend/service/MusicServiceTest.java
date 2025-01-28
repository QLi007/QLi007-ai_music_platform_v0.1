package com.aimusic.backend.service;

import com.aimusic.backend.domain.dto.MusicDTO;
import com.aimusic.backend.domain.dto.MusicGenerationRequest;
import com.aimusic.backend.domain.entity.Music;
import com.aimusic.backend.domain.enums.MusicStatus;
import com.aimusic.backend.domain.entity.User;
import com.aimusic.backend.domain.repository.MusicRepository;
import com.aimusic.backend.domain.repository.UserRepository;
import com.aimusic.backend.domain.service.MusicService;
import com.aimusic.backend.domain.service.impl.MusicServiceImpl;
import com.aimusic.backend.exception.EntityNotFoundException;
import com.aimusic.backend.utils.MusicTestFactory;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 音乐服务测试类
 */
@ExtendWith(MockitoExtension.class)
class MusicServiceTest {

    @Mock
    private MusicRepository musicRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private MusicServiceImpl musicService;

    private Music mockMusic;
    private MusicGenerationRequest mockRequest;
    private User mockUser;
    private UUID mockId;

    @BeforeEach
    void setUp() {
        mockId = UUID.randomUUID();
        mockUser = MusicTestFactory.createTestUser();
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        
        // 初始化测试用的Music实体
        mockMusic = Music.builder()
                .id(mockId)
                .user(mockUser)
                .prompt("测试提示词")
                .style("古典")
                .duration(60)
                .status(MusicStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 初始化测试用的请求对象
        mockRequest = MusicGenerationRequest.builder()
                .prompt("测试提示词")
                .style("古典")
                .duration(60)
                .userId(mockUser.getId())
                .build();
    }

    @Test
    void generateMusic_ShouldCreateNewMusic() {
        // Arrange
        MusicGenerationRequest request = MusicGenerationRequest.builder()
                .prompt("测试音乐")
                .style("古典")
                .duration(60)
                .userId(mockUser.getId())
                .build();
        
        when(musicRepository.save(any(Music.class))).thenReturn(mockMusic);
        
        // Act
        MusicDTO result = musicService.generateMusic(request);
        
        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(request.getPrompt(), result.getPrompt());
        assertEquals(request.getStyle(), result.getStyle());
        assertEquals(request.getDuration(), result.getDuration());
        assertEquals(request.getUserId(), result.getUserId());
        
        verify(musicRepository).save(any(Music.class));
    }

    @Test
    void generateMusic_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> musicService.generateMusic(mockRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("用户不存在");
    }

    @Test
    void getMusicById_ShouldReturnMusic_WhenMusicExists() {
        // Arrange
        when(musicRepository.findById(any(UUID.class))).thenReturn(Optional.of(mockMusic));
        
        // Act
        MusicDTO result = musicService.getMusicById(mockMusic.getId());
        
        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(mockMusic.getPrompt(), result.getPrompt());
        assertEquals(mockMusic.getStyle(), result.getStyle());
        assertEquals(mockMusic.getDuration(), result.getDuration());
        assertEquals(mockMusic.getUserId(), result.getUserId());
    }

    @Test
    void getMusicById_ShouldThrowException_WhenMusicNotFound() {
        // Arrange
        when(musicRepository.findById(mockId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> musicService.getMusicById(mockId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("音乐不存在");
    }

    @Test
    void listMusic_ShouldReturnPageOfMusic() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Music> musicList = Collections.singletonList(mockMusic);
        Page<Music> musicPage = new PageImpl<>(musicList, pageable, 1);
        
        when(musicRepository.findAllByOrderByCreatedAtDesc(pageable))
                .thenReturn(musicPage);
        
        // Act
        Page<MusicDTO> result = musicService.listMusic(pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        
        MusicDTO musicDTO = result.getContent().get(0);
        assertNotNull(musicDTO);
        assertEquals(mockMusic.getPrompt(), musicDTO.getPrompt());
        assertEquals(mockMusic.getStyle(), musicDTO.getStyle());
        assertEquals(mockMusic.getDuration(), musicDTO.getDuration());
        assertEquals(mockMusic.getUserId(), musicDTO.getUserId());
    }

    @Test
    void updateMusicStatus_ShouldUpdateStatus_WhenMusicExists() {
        // Arrange
        String audioUrl = "http://example.com/music.mp3";
        when(musicRepository.findById(mockId)).thenReturn(Optional.of(mockMusic));
        when(musicRepository.save(any(Music.class))).thenReturn(mockMusic);

        // Act
        MusicDTO result = musicService.updateMusicStatus(mockId, audioUrl);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(MusicStatus.COMPLETED);
        assertThat(result.getAudioUrl()).isEqualTo(audioUrl);
        verify(musicRepository).findById(mockId);
        verify(musicRepository).save(any(Music.class));
    }

    @Test
    void updateMusicStatus_ShouldThrowException_WhenMusicNotExists() {
        // Arrange
        String audioUrl = "http://example.com/music.mp3";
        when(musicRepository.findById(mockId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> musicService.updateMusicStatus(mockId, audioUrl))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("音乐不存在");
    }
} 