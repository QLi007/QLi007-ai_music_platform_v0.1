package com.aimusic.backend.repository;

import com.aimusic.backend.domain.entity.Music;
import com.aimusic.backend.domain.entity.MusicStatus;
import com.aimusic.backend.domain.repository.MusicRepository;
import com.aimusic.backend.utils.MusicTestFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 音乐仓库测试类
 */
@DataJpaTest
@ActiveProfiles("test")
class MusicRepositoryTest {

    @Autowired
    private MusicRepository musicRepository;

    @Test
    void shouldSaveMusic() {
        // Given
        Music music = MusicTestFactory.createMusic();

        // When
        Music savedMusic = musicRepository.save(music);

        // Then
        assertThat(savedMusic.getId()).isNotNull();
        assertThat(savedMusic.getTitle()).isEqualTo(music.getTitle());
        assertThat(savedMusic.getPrompt()).isEqualTo(music.getPrompt());
        assertThat(savedMusic.getStyle()).isEqualTo(music.getStyle());
        assertThat(savedMusic.getDuration()).isEqualTo(music.getDuration());
        assertThat(savedMusic.getStatus()).isEqualTo(MusicStatus.COMPLETED);
    }

    @Test
    void shouldFindMusicByOrderByCreatedAtDesc() {
        // Given
        Music music1 = musicRepository.save(MusicTestFactory.createMusic());
        Music music2 = musicRepository.save(MusicTestFactory.createMusic());

        // When
        Page<Music> result = musicRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getCreatedAt())
                .isAfterOrEqualTo(result.getContent().get(1).getCreatedAt());
    }
} 