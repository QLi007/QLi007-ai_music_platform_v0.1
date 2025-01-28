package com.aimusic.backend.client;

import com.aimusic.backend.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Suno API客户端 - MVP版本
 * 只实现最基本的音乐生成功能
 */
@Slf4j
@Component
public class SunoApiClient {

    private final RestTemplate restTemplate;
    
    @Value("${suno.api.url}")
    private String apiUrl;
    
    @Value("${suno.api.cookie}")
    private String cookie;

    public SunoApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 生成音乐
     * @param prompt 提示词
     * @return 生成结果
     */
    @Retryable(
        value = {RestClientException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public String generateMusic(String prompt) {
        log.info("开始生成音乐, prompt: {}", prompt);
        
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new IllegalArgumentException("提示词不能为空");
        }

        try {
            Map<String, Object> requestBody = Map.of(
                "prompt", prompt,
                "wait_audio", false,
                "make_instrumental", false
            );

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            headers.add("Cookie", cookie);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                apiUrl + "/generate",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                throw new ExternalApiException("API响应为空");
            }

            return (String) responseBody.get("id");
        } catch (RestClientException e) {
            log.error("生成音乐失败", e);
            throw new ExternalApiException("API调用失败: " + e.getMessage());
        }
    }

    /**
     * 获取音乐状态
     * @param musicId 音乐ID
     * @return 音乐状态
     */
    @Retryable(
        value = {RestClientException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public Map<String, Object> getMusicStatus(String musicId) {
        log.info("获取音乐状态, musicId: {}", musicId);
        
        if (musicId == null || musicId.trim().isEmpty()) {
            throw new IllegalArgumentException("音乐ID不能为空");
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie", cookie);
            HttpEntity<?> request = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                apiUrl + "/get?ids=" + musicId,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                throw new ExternalApiException("API响应为空");
            }

            return responseBody;
        } catch (RestClientException e) {
            log.error("获取音乐状态失败", e);
            throw new ExternalApiException("API调用失败: " + e.getMessage());
        }
    }

    /**
     * 获取配额信息
     * @return 配额信息
     */
    @Retryable(
        value = {RestClientException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public Map<String, Object> getQuota() {
        log.info("获取配额信息");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie", cookie);
            HttpEntity<?> request = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                apiUrl + "/quota",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                throw new ExternalApiException("API响应为空");
            }

            return responseBody;
        } catch (RestClientException e) {
            log.error("获取配额信息失败", e);
            throw new ExternalApiException("API调用失败: " + e.getMessage());
        }
    }
} 