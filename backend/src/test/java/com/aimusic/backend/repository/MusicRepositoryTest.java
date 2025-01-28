package com.aimusic.backend.repository;

import com.aimusic.backend.domain.entity.Music;
import com.aimusic.backend.domain.enums.MusicStatus;
import com.aimusic.backend.domain.entity.User;
import com.aimusic.backend.domain.repository.MusicRepository;
import com.aimusic.backend.domain.repository.UserRepository;
import com.aimusic.backend.utils.MusicTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 音乐仓库测试类
 */
@DataJpaTest
@ActiveProfiles("test")
class MusicRepositoryTest {

    @Autowired
    private MusicRepository musicRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = MusicTestFactory.createTestUser();
        testUser = userRepository.save(testUser);
    }

    @Test
    void shouldSaveMusic() {
        // Arrange
        Music music = MusicTestFactory.createTestMusic(testUser);

        // Act
        Music savedMusic = musicRepository.save(music);

        // Assert
        assertThat(savedMusic.getId()).isNotNull();
        assertThat(savedMusic.getPrompt()).isEqualTo(music.getPrompt());
        assertThat(savedMusic.getStyle()).isEqualTo(music.getStyle());
        assertThat(savedMusic.getDuration()).isEqualTo(music.getDuration());
        assertThat(savedMusic.getStatus()).isEqualTo(MusicStatus.PENDING);
        assertThat(savedMusic.getAudioUrl()).isEqualTo(music.getAudioUrl());
        assertThat(savedMusic.getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void shouldFindMusicByOrderByCreatedAtDesc() {
        // Arrange
        musicRepository.save(MusicTestFactory.createTestMusic(testUser));
        musicRepository.save(MusicTestFactory.createTestMusic(testUser));

        // Act
        Page<Music> result = musicRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 10));

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getCreatedAt())
                .isAfterOrEqualTo(result.getContent().get(1).getCreatedAt());
    }

    @Test
    void shouldReturnEmptyPage_WhenNoMusicExists() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);

        // Act
        Page<Music> result = musicRepository.findAllByOrderByCreatedAtDesc(pageRequest);

        // Assert
        assertThat(result).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void shouldFindById_WhenMusicExists() {
        // Arrange
        Music savedMusic = musicRepository.save(MusicTestFactory.createTestMusic(testUser));

        // Act
        Optional<Music> result = musicRepository.findById(savedMusic.getId());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedMusic.getId());
        assertThat(result.get().getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void shouldReturnEmpty_WhenFindByNonExistentId() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        Optional<Music> result = musicRepository.findById(nonExistentId);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void shouldUpdateMusicStatus() {
        // Arrange
        Music music = musicRepository.save(MusicTestFactory.createTestMusic(testUser));
        music.setStatus(MusicStatus.PROCESSING);
        music.setUpdatedAt(LocalDateTime.now());

        // Act
        Music updatedMusic = musicRepository.save(music);

        // Assert
        assertThat(updatedMusic.getStatus()).isEqualTo(MusicStatus.PROCESSING);
        assertThat(updatedMusic.getUpdatedAt()).isAfterOrEqualTo(music.getCreatedAt());
    }

    @Test
    void shouldDeleteMusic() {
        // Arrange
        Music music = musicRepository.save(MusicTestFactory.createTestMusic(testUser));

        // Act
        musicRepository.delete(music);
        Optional<Music> result = musicRepository.findById(music.getId());

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandlePagination() {
        // Arrange
        for (int i = 0; i < 15; i++) {
            musicRepository.save(MusicTestFactory.createTestMusic(testUser));
        }

        // Act
        Page<Music> firstPage = musicRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 10));
        Page<Music> secondPage = musicRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(1, 10));

        // Assert
        assertThat(firstPage.getContent()).hasSize(10);
        assertThat(secondPage.getContent()).hasSize(5);
        assertThat(firstPage.getTotalElements()).isEqualTo(15);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
    }
} 