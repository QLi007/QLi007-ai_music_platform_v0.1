package com.aimusic.backend.domain.repository;

import com.aimusic.backend.domain.entity.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 音乐仓库接口
 * 
 * @author AI Music Team
 * @version 0.1.0
 */
@Repository
public interface MusicRepository extends JpaRepository<Music, UUID> {
    
    /**
     * 按创建时间降序查询所有音乐
     *
     * @param pageable 分页参数
     * @return 音乐分页结果
     */
    Page<Music> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Optional<Music> findByGenerationId(String generationId);

    List<Music> findByUserId(UUID userId);

    Page<Music> findByUserId(UUID userId, Pageable pageable);
} 