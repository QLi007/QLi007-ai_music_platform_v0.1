package com.aimusic.backend.domain.repository;

import com.aimusic.backend.domain.entity.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * 音乐仓储接口
 * 
 * @author AI Music Team
 * @version 0.1.0
 */
@Repository
public interface MusicRepository extends JpaRepository<Music, UUID> {
    
    /**
     * 分页查询所有音乐，按创建时间倒序排序
     *
     * @param pageable 分页参数
     * @return 音乐分页结果
     */
    Page<Music> findAllByOrderByCreatedAtDesc(Pageable pageable);
} 