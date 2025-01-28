package com.aimusic.backend.config;

import com.aimusic.backend.exception.ExternalApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * RestTemplate响应错误处理器
 * 处理HTTP请求的错误响应
 */
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || 
               response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        String responseBody = new BufferedReader(
            new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));

        if (response.getStatusCode().is4xxClientError()) {
            throw new ExternalApiException("外部API客户端错误: " + responseBody);
        } else if (response.getStatusCode().is5xxServerError()) {
            throw new ExternalApiException("外部API服务器错误: " + responseBody);
        }
    }
} 