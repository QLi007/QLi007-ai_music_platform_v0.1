package com.aimusic.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway Application
 * 
 * 作为系统的统一入口点，负责：
 * 1. 路由请求到相应的微服务
 * 2. 身份验证和授权
 * 3. 请求限流和熔断
 * 4. 请求/响应转换
 * 5. 监控和日志
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
} 