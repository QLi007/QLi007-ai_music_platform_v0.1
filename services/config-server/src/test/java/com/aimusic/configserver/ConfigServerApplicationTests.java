package com.aimusic.configserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Config Server Application Tests
 * 
 * 测试配置服务器的基本功能：
 * 1. 上下文加载
 * 2. 配置访问
 * 3. 安全设置
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ConfigServerApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        // 验证Spring上下文是否正确加载
    }

    @Test
    void configEndpointShouldBeSecured() {
        // 验证配置端点是否受到安全保护
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/config/default",
                String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void actuatorEndpointShouldBeAccessible() {
        // 验证Actuator健康检查端点是否可访问
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/actuator/health",
                String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
} 