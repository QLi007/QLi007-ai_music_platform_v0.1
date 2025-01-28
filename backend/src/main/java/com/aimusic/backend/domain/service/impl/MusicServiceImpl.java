package com.aimusic.backend.domain.service.impl;

import com.aimusic.backend.domain.dto.MusicDTO;
import com.aimusic.backend.domain.dto.MusicGenerationRequest;
import com.aimusic.backend.domain.entity.Music;
import com.aimusic.backend.domain.enums.MusicStatus;
import com.aimusic.backend.domain.entity.User;
import com.aimusic.backend.domain.repository.MusicRepository;
import com.aimusic.backend.domain.repository.UserRepository;
import com.aimusic.backend.domain.service.MusicService;
import com.aimusic.backend.exception.EntityNotFoundException;
import com.aimusic.backend.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 音乐服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MusicServiceImpl implements MusicService {

    private final MusicRepository musicRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    @Override
    public MusicDTO generateMusic(MusicGenerationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("用户不存在"));

        Music music = Music.builder()
                .prompt(request.getPrompt())
                .style(request.getStyle())
                .duration(request.getDuration())
                .status(MusicStatus.PENDING)
                .lyrics(request.isGenerateLyrics() ? "" : null)
                .user(user)
                .build();

        music = musicRepository.save(music);
        return convertToDTO(music);
    }

    @Override
    @Transactional(readOnly = true)
    public MusicDTO getMusicById(UUID id) {
        return musicRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("音乐不存在"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MusicDTO> listMusic(Pageable pageable) {
        return musicRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public MusicDTO updateMusicStatus(UUID id, String url) {
        Music music = musicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("音乐不存在"));
        
        music.setStatus(MusicStatus.COMPLETED);
        music.setAudioUrl(url);
        music = musicRepository.save(music);
        return convertToDTO(music);
    }

    @Override
    @Transactional
    public void deleteMusic(UUID id) {
        Music music = musicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Music not found with id: " + id));

        // 如果存在音频文件，删除它
        if (music.getAudioUrl() != null) {
            String filename = music.getAudioUrl().substring(music.getAudioUrl().lastIndexOf('/') + 1);
            if (storageService.exists(filename)) {
                storageService.delete(filename);
            }
        }

        musicRepository.delete(music);
        log.info("Music deleted successfully: {}", id);
    }

    private MusicDTO convertToDTO(Music music) {
        return MusicDTO.builder()
                .id(music.getId())
                .userId(music.getUserId())
                .prompt(music.getPrompt())
                .style(music.getStyle())
                .status(music.getStatus())
                .audioUrl(music.getAudioUrl())
                .lyrics(music.getLyrics())
                .duration(music.getDuration())
                .errorMessage(music.getErrorMessage())
                .generationId(music.getGenerationId())
                .createdAt(music.getCreatedAt())
                .updatedAt(music.getUpdatedAt())
                .build();
    }
} 