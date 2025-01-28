package com.aimusic.backend.domain.service;

import com.aimusic.backend.domain.dto.MusicDTO;
import com.aimusic.backend.domain.dto.MusicGenerationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * 音乐服务接口
 * 
 * @author AI Music Team
 * @version 0.1.0
 */
public interface MusicService {
    
    /**
     * 生成音乐
     *
     * @param request 音乐生成请求
     * @return 音乐DTO
     */
    MusicDTO generateMusic(MusicGenerationRequest request);
    
    /**
     * 根据ID获取音乐
     *
     * @param id 音乐ID
     * @return 音乐DTO
     */
    MusicDTO getMusicById(UUID id);
    
    /**
     * 分页获取音乐列表
     *
     * @param pageable 分页参数
     * @return 音乐DTO分页对象
     */
    Page<MusicDTO> listMusic(Pageable pageable);
    
    /**
     * 更新音乐状态
     *
     * @param id 音乐ID
     * @param url 音频URL
     * @return 更新后的音乐DTO
     */
    MusicDTO updateMusicStatus(UUID id, String url);

    /**
     * 删除音乐
     *
     * @param id 音乐ID
     */
    void deleteMusic(UUID id);
} 