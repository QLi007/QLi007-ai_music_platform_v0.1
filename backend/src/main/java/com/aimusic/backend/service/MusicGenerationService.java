package com.aimusic.backend.service;

import com.aimusic.backend.domain.dto.MusicGenerationRequest;
import com.aimusic.backend.domain.dto.MusicDTO;

import java.util.concurrent.CompletableFuture;

/**
 * 音乐生成服务接口
 * 
 * @author AI Music Team
 * @version 1.0
 * @date 2024-03-21
 */
public interface MusicGenerationService {
    
    /**
     * 异步生成音乐
     *
     * @param request 音乐生成请求
     * @return 异步结果,包含生成的音乐信息
     */
    CompletableFuture<MusicDTO> generateMusic(MusicGenerationRequest request);
    
    /**
     * 获取音乐生成状态
     *
     * @param musicId 音乐ID
     * @return 音乐信息
     */
    MusicDTO getMusicStatus(String musicId);
    
    /**
     * 取消音乐生成
     *
     * @param musicId 音乐ID
     * @return 取消后的音乐信息
     */
    MusicDTO cancelGeneration(String musicId);
} 