package com.aimusic.backend.domain.service.impl;

import com.aimusic.backend.client.SunoApiClient;
import com.aimusic.backend.domain.dto.MusicDTO;
import com.aimusic.backend.domain.dto.MusicGenerationRequest;
import com.aimusic.backend.domain.entity.Music;
import com.aimusic.backend.domain.enums.MusicStatus;
import com.aimusic.backend.domain.repository.MusicRepository;
import com.aimusic.backend.domain.service.MusicGenerationService;
import com.aimusic.backend.exception.EntityNotFoundException;
import com.aimusic.backend.exception.MusicGenerationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 音乐生成服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MusicGenerationServiceImpl implements MusicGenerationService {

    private final MusicRepository musicRepository;
    private final SunoApiClient sunoApiClient;

    @Override
    @Async
    @Transactional
    public CompletableFuture<MusicDTO> generateMusic(MusicGenerationRequest request) {
        log.info("开始生成音乐, request: {}", request);
        
        // 创建Music实体并设置UUID
        Music music = Music.builder()
                .id(UUID.randomUUID())
                .prompt(request.getPrompt())
                .style(request.getStyle())
                .duration(request.getDuration())
                .status(MusicStatus.PROCESSING) // 直接设置为PROCESSING状态
                .userId(request.getUserId())
                .build();
        
        // 保存实体获取ID
        music = musicRepository.save(music);
        if (music == null) {
            throw new MusicGenerationException("保存音乐记录失败");
        }
        String musicId = music.getId().toString();
        log.info("创建音乐记录成功, musicId: {}", musicId);
        
        try {
            // 调用API生成音乐,最多重试1次
            String generationId = null;
            Exception lastException = null;
            int maxAttempts = 2;
            
            for (int attempt = 0; attempt < maxAttempts; attempt++) {
                try {
                    generationId = sunoApiClient.generateMusic(request.getPrompt());
                    if (generationId != null) {
                        break;
                    }
                } catch (Exception e) {
                    lastException = e;
                    log.warn("第{}次生成音乐失败, musicId: {}, error: {}", attempt + 1, musicId, e.getMessage());
                    if (attempt < maxAttempts - 1) { // 如果不是最后一次尝试,等待一段时间后重试
                        Thread.sleep(1000); // 等待1秒后重试
                    }
                }
            }
            
            // 检查最终结果
            if (generationId == null) {
                String errorMsg = lastException != null ? 
                        String.format("生成音乐失败: %s", lastException.getMessage()) :
                        "生成音乐失败: API响应为空";
                log.error("生成音乐失败: {}", errorMsg);
                music.setStatus(MusicStatus.FAILED);
                music.setErrorMessage(errorMsg);
                music = musicRepository.save(music);
                throw new MusicGenerationException(errorMsg, lastException);
            }
            
            // 更新generation_id
            music.setGenerationId(generationId);
            music = musicRepository.save(music);
            log.info("音乐生成任务创建成功, musicId: {}, generationId: {}", musicId, generationId);
            
            return CompletableFuture.completedFuture(MusicDTO.fromEntity(music));
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            String errorMsg = "生成音乐被中断";
            log.error("生成音乐失败, musicId: {}, error: {}", musicId, errorMsg);
            music.setStatus(MusicStatus.FAILED);
            music.setErrorMessage(errorMsg);
            music = musicRepository.save(music);
            throw new MusicGenerationException(errorMsg, e);
        } catch (MusicGenerationException e) {
            log.error("生成音乐失败, musicId: {}, error: {}", musicId, e.getMessage());
            music.setStatus(MusicStatus.FAILED);
            music.setErrorMessage(e.getMessage());
            music = musicRepository.save(music);
            throw e;
        } catch (Exception e) {
            String errorMsg = String.format("生成音乐时发生未知错误: %s", e.getMessage());
            log.error("生成音乐失败, musicId: {}, error: {}", musicId, errorMsg, e);
            music.setStatus(MusicStatus.FAILED);
            music.setErrorMessage(errorMsg);
            music = musicRepository.save(music);
            throw new MusicGenerationException(errorMsg, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MusicDTO getMusicStatus(String musicId) {
        log.info("获取音乐状态, musicId: {}", musicId);
        
        try {
            // 先检查 musicId 是否为空
            if (musicId == null || musicId.trim().isEmpty()) {
                throw new IllegalArgumentException("音乐ID不能为空");
            }
            
            // 尝试获取音乐状态
            Music music = musicRepository.findById(UUID.fromString(musicId))
                    .orElseThrow(() -> new EntityNotFoundException("音乐不存在"));
            
            // 如果状态无效,抛出异常
            if (music.getStatus() == null) {
                throw new MusicGenerationException("无效的音乐状态: null");
            }
            
            return MusicDTO.fromEntity(music);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Invalid UUID")) {
                throw new MusicGenerationException("无效的音乐ID格式");
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public MusicDTO cancelGeneration(String musicId) {
        log.info("取消音乐生成, musicId: {}", musicId);

        try {
            Music music = musicRepository.findById(UUID.fromString(musicId))
                    .orElseThrow(() -> new EntityNotFoundException("音乐不存在"));

            music.setStatus(MusicStatus.CANCELLED);
            music = musicRepository.save(music);

            return MusicDTO.fromEntity(music);
        } catch (IllegalArgumentException e) {
            throw new MusicGenerationException("无效的音乐ID格式");
        }
    }
} 