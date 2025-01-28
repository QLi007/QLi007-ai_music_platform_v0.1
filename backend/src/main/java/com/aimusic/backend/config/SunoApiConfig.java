package com.aimusic.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Suno API配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "suno.api")
public class SunoApiConfig {
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * API基础URL
     */
    private String baseUrl;
    
    /**
     * 请求超时时间（毫秒）
     */
    private int timeout = 30000;
    
    /**
     * 最大重试次数
     */
    private int maxRetries = 3;
} 