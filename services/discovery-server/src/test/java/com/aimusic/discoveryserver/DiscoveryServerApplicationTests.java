package com.aimusic.discoveryserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class DiscoveryServerApplicationTests {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    void contextLoads() {
        // 验证Spring上下文是否正确加载
    }

    @Test
    void eurekaEndpointShouldBeSecured() {
        // 验证Eureka端点是否受到安全保护
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/eureka/apps",
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