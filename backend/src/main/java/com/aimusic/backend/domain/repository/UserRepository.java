package com.aimusic.backend.domain.repository;

import com.aimusic.backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * 用户数据访问接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
} 