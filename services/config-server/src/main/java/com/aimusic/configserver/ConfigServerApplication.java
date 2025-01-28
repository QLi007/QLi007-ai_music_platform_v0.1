package com.aimusic.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Configuration Server Application
 * 
 * 配置服务器负责：
 * 1. 集中管理所有服务的配置
 * 2. 支持配置的版本控制
 * 3. 实现配置的动态刷新
 * 4. 提供配置加密功能
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
} 