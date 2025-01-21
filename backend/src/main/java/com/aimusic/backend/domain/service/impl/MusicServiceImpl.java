package com.aimusic.backend.domain.service.impl;

import com.aimusic.backend.domain.dto.MusicDTO;
import com.aimusic.backend.domain.dto.MusicGenerationRequest;
import com.aimusic.backend.domain.entity.Music;
import com.aimusic.backend.domain.entity.MusicStatus;
import com.aimusic.backend.domain.repository.MusicRepository;
import com.aimusic.backend.domain.service.MusicService;
import com.aimusic.backend.exception.EntityNotFoundException;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 音乐服务实现类
 * 
 * @author AI Music Team
 * @version 0.1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {

    private final MusicRepository musicRepository;
    
    @Override
    @Transactional
    @Timed(value = "music.generation.request", description = "音乐生成请求处理时间")
    public MusicDTO generateMusic(MusicGenerationRequest request) {
        log.info("开始生成音乐, request: {}", request);
        
        // 创建音乐记录
        Music music = new Music();
        music.setPrompt(request.getPrompt());
        music.setStyle(request.getStyle());
        music.setDuration(request.getDuration());
        music.setStatus(MusicStatus.PENDING);
        
        // 保存音乐记录
        Music savedMusic = musicRepository.save(music);
        log.info("音乐生成任务创建成功, id: {}", savedMusic.getId());
        
        // 异步执行音乐生成
        generateMusicAsync(savedMusic.getId());
        
        return convertToDTO(savedMusic);
    }
    
    @Async("musicGenerationExecutor")
    protected CompletableFuture<Void> generateMusicAsync(UUID musicId) {
        return CompletableFuture.runAsync(() -> {
            try {
                log.info("开始异步生成音乐, id: {}", musicId);
                
                // 更新状态为生成中
                Music music = musicRepository.findById(musicId)
                        .orElseThrow(() -> new EntityNotFoundException("音乐不存在"));
                music.setStatus(MusicStatus.GENERATING);
                musicRepository.save(music);
                
                // TODO: 调用AI服务生成音乐
                // 这里模拟生成过程
                Thread.sleep(5000);
                
                // 生成成功，更新状态和URL
                music.setStatus(MusicStatus.COMPLETED);
                music.setUrl("http://example.com/music/" + musicId + ".mp3");
                musicRepository.save(music);
                
                log.info("音乐生成完成, id: {}", musicId);
            } catch (Exception e) {
                log.error("音乐生成失败, id: {}", musicId, e);
                try {
                    // 更新状态为失败
                    Music music = musicRepository.findById(musicId)
                            .orElseThrow(() -> new EntityNotFoundException("音乐不存在"));
                    music.setStatus(MusicStatus.FAILED);
                    musicRepository.save(music);
                } catch (Exception ex) {
                    log.error("更新音乐状态失败, id: {}", musicId, ex);
                }
            }
        });
    }
    
    @Override
    @Transactional(readOnly = true)
    @Timed(value = "music.get.by.id", description = "根据ID获取音乐信息的处理时间")
    public MusicDTO getMusicById(UUID id) {
        log.info("查询音乐详情, id: {}", id);
        
        return musicRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("音乐不存在"));
    }
    
    @Override
    @Transactional(readOnly = true)
    @Timed(value = "music.list", description = "分页查询音乐列表的处理时间")
    public Page<MusicDTO> listMusic(Pageable pageable) {
        log.info("分页查询音乐列表, pageable: {}", pageable);
        
        return musicRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional
    @Timed(value = "music.status.update", description = "更新音乐状态的处理时间")
    public void updateMusicStatus(UUID id, String url) {
        log.info("更新音乐状态, id: {}, url: {}", id, url);
        
        Music music = musicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("音乐不存在"));
        
        music.setStatus(MusicStatus.COMPLETED);
        music.setUrl(url);
        
        musicRepository.save(music);
        log.info("音乐状态更新成功");
    }
    
    /**
     * 将实体转换为DTO
     */
    private MusicDTO convertToDTO(Music music) {
        MusicDTO dto = new MusicDTO();
        dto.setId(music.getId());
        dto.setPrompt(music.getPrompt());
        dto.setStyle(music.getStyle());
        dto.setDuration(music.getDuration());
        dto.setStatus(music.getStatus());
        dto.setUrl(music.getUrl());
        dto.setCreatedAt(music.getCreatedAt());
        dto.setUpdatedAt(music.getUpdatedAt());
        return dto;
    }
} 