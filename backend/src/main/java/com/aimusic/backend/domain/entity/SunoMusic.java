package com.aimusic.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Suno API生成的音乐实体类
 */
@Entity
@Table(name = "suno_music")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SunoMusic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Suno API的生成任务ID
     */
    @Column(name = "generation_id")
    private String generationId;

    /**
     * 生成提示词
     */
    @Column(nullable = false)
    private String prompt;

    /**
     * 音乐风格
     */
    private String style;

    /**
     * 音乐时长(秒)
     */
    private Integer duration;

    /**
     * 生成状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MusicStatus status;

    /**
     * 音频文件URL
     */
    @Column(name = "audio_url")
    private String audioUrl;

    /**
     * 错误信息
     */
    @Column(name = "error_message")
    private String errorMessage;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private String userId;
} 