package com.aimusic.backend.domain.entity;

import jakarta.persistence.*;
import com.aimusic.backend.domain.dto.MusicDTO;
import com.aimusic.backend.domain.enums.MusicStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 音乐实体类
 * 
 * @author AI Music Team
 * @version 0.1.0
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "music")
@EntityListeners(AuditingEntityListener.class)
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private UUID userId;

    /**
     * 提示词
     */
    @Column(nullable = false)
    private String prompt;

    /**
     * 生成任务ID
     */
    @Column(name = "generation_id")
    private String generationId;

    /**
     * 音频URL
     */
    @Column(name = "audio_url")
    private String audioUrl;

    /**
     * 歌词
     */
    private String lyrics;

    /**
     * 音乐时长（秒）
     */
    private Integer duration;

    /**
     * 音乐风格
     */
    private String style;

    /**
     * 生成状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MusicStatus status;

    /**
     * 错误信息
     */
    @Column(name = "error_message")
    private String errorMessage;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.user != null) {
            this.userId = this.user.getId();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.user != null) {
            this.userId = this.user.getId();
        }
        updatedAt = LocalDateTime.now();
    }

    /**
     * 转换为 DTO
     *
     * @return MusicDTO
     */
    public MusicDTO toDTO() {
        return MusicDTO.builder()
                .id(this.id)
                .userId(this.userId)
                .prompt(this.prompt)
                .style(this.style)
                .status(this.status)
                .audioUrl(this.audioUrl)
                .lyrics(this.lyrics)
                .duration(this.duration)
                .errorMessage(this.errorMessage)
                .generationId(this.generationId)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
} 