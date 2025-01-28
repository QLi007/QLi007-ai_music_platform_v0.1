package com.aimusic.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI音乐平台应用程序入口类
 * 
 * @author AI Music Team
 * @version 1.0
 */
@SpringBootApplication
public class AiMusicApplication {
    
    /**
     * 应用程序主入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(AiMusicApplication.class, args);
    }
} 