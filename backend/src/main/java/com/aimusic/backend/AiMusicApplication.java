package com.aimusic.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * AI音乐平台后端应用程序入口
 * 
 * @author AI Music Team
 * @version 0.1.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class AiMusicApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiMusicApplication.class, args);
    }
} 