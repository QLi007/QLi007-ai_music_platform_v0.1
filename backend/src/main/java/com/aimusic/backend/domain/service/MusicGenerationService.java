package com.aimusic.backend.domain.service;

import com.aimusic.backend.domain.dto.MusicDTO;
import com.aimusic.backend.domain.dto.MusicGenerationRequest;

import java.util.concurrent.CompletableFuture;

/**
 * 音乐生成服务接口
 * 处理音乐生成相关的业务逻辑
 */
public interface MusicGenerationService {

    /**
     * 生成音乐
     *
     * @param request 音乐生成请求
     * @return 音乐DTO的Future对象
     */
    CompletableFuture<MusicDTO> generateMusic(MusicGenerationRequest request);

    /**
     * 获取音乐生成状态
     *
     * @param musicId 音乐ID
     * @return 音乐DTO
     */
    MusicDTO getMusicStatus(String musicId);

    /**
     * 取消音乐生成
     *
     * @param musicId 音乐ID
     * @return 音乐DTO
     */
    MusicDTO cancelGeneration(String musicId);
} 