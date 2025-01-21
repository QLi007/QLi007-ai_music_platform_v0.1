package com.aimusic.backend.domain.entity;

/**
 * 音乐状态枚举
 * 
 * @author AI Music Team
 * @version 0.1.0
 */
public enum MusicStatus {
    /**
     * 等待生成
     */
    PENDING,
    
    /**
     * 生成中
     */
    GENERATING,
    
    /**
     * 生成完成
     */
    COMPLETED,
    
    /**
     * 生成失败
     */
    FAILED
} 