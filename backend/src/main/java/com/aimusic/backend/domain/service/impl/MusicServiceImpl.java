package com.aimusic.backend.domain.service.impl;

import com.aimusic.backend.domain.dto.MusicDTO;
import com.aimusic.backend.domain.dto.MusicGenerationRequest;
import com.aimusic.backend.domain.entity.Music;
import com.aimusic.backend.domain.entity.MusicStatus;
import com.aimusic.backend.domain.repository.MusicRepository;
import com.aimusic.backend.domain.service.MusicService;
import com.aimusic.backend.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 音乐服务实现类
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MusicServiceImpl implements MusicService {

    private final MusicRepository musicRepository;

    @Override
    public MusicDTO generateMusic(MusicGenerationRequest request) {
        Music music = Music.builder()
                .title(request.getTitle())
                .prompt(request.getPrompt())
                .style(request.getStyle())
                .duration(request.getDuration())
                .status(MusicStatus.PENDING)
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
    public void updateMusicStatus(UUID id, String url) {
        Music music = musicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("音乐不存在"));
        
        music.setStatus(MusicStatus.COMPLETED);
        music.setUrl(url);
        musicRepository.save(music);
    }

    private MusicDTO convertToDTO(Music music) {
        return MusicDTO.builder()
                .id(music.getId())
                .title(music.getTitle())
                .prompt(music.getPrompt())
                .style(music.getStyle())
                .duration(music.getDuration())
                .status(music.getStatus())
                .url(music.getUrl())
                .createdAt(music.getCreatedAt())
                .updatedAt(music.getUpdatedAt())
                .build();
    }
} 