package com.aimusic.backend.domain.enums;

public enum MusicStatus {
    PENDING,     // 等待生成
    PROCESSING,  // 生成中
    COMPLETED,   // 生成完成
    FAILED,      // 生成失败
    CANCELLED    // 已取消
} 