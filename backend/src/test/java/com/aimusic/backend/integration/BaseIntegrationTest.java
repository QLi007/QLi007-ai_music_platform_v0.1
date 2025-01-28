package com.aimusic.backend.integration;

import com.aimusic.backend.AiMusicApplication;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * 集成测试基类
 * 提供所有集成测试需要的基础配置和通用功能
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = AiMusicApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    // 通用测试配置和工具方法
} 