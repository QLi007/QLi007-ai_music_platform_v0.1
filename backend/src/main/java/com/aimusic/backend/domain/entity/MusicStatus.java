package com.aimusic.backend.domain.entity;

/**
 * 音乐生成状态枚举
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
     * 生成成功
     */
    COMPLETED,
    
    /**
     * 生成失败
     */
    FAILED,
    
    /**
     * 已取消
     */
    CANCELLED
} 