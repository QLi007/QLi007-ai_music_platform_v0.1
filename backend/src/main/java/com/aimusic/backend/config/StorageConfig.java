package com.aimusic.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * 文件存储配置类
 * 配置文件存储相关参数
 */
@Configuration
@ConfigurationProperties(prefix = "storage")
@Data
public class StorageConfig {
    
    /**
     * 存储根路径
     */
    private String location;
    
    /**
     * 最大文件大小（字节）
     */
    private long maxFileSize;
    
    /**
     * 允许的文件类型
     */
    private Set<String> allowedFileTypes;
    
    /**
     * 文件URL前缀
     */
    private String urlPrefix;
    
    /**
     * 临时文件目录
     */
    private String tempDir;
    
    /**
     * 是否使用原始文件名
     */
    private boolean useOriginalFilename;
    
    /**
     * 文件名最大长度
     */
    private int maxFilenameLength;
} 