package com.aimusic.backend.client;

import com.aimusic.backend.exception.ExternalApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.mockito.ArgumentMatchers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SunoApiClient测试类 - MVP版本
 */
@ExtendWith(MockitoExtension.class)
class SunoApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SunoApiClient sunoApiClient;

    private static final String API_URL = "http://api.suno.ai";
    private static final String COOKIE = "test-cookie";

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        sunoApiClient = new SunoApiClient(restTemplate);
        ReflectionTestUtils.setField(sunoApiClient, "apiUrl", API_URL);
        ReflectionTestUtils.setField(sunoApiClient, "cookie", COOKIE);
    }

    @Test
    void generateMusic_Success() {
        // Given
        String prompt = "test prompt";
        Map<String, Object> requestBody = Map.of(
            "prompt", prompt,
            "wait_audio", false,
            "make_instrumental", false
        );
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Cookie", COOKIE);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        Map<String, Object> responseBody = Map.of("id", "test-id");
        ResponseEntity<Map<String, Object>> response = ResponseEntity.ok(responseBody);
        
        when(restTemplate.exchange(
            eq(API_URL + "/generate"),
            eq(HttpMethod.POST),
            argThat(req -> req.getHeaders().equals(headers) && req.getBody().equals(requestBody)),
            any(ParameterizedTypeReference.class)
        )).thenReturn(response);

        // When
        String result = sunoApiClient.generateMusic(prompt);

        // Then
        assertEquals("test-id", result);
        verify(restTemplate).exchange(
            eq(API_URL + "/generate"),
            eq(HttpMethod.POST),
            argThat(req -> req.getHeaders().equals(headers) && req.getBody().equals(requestBody)),
            any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void generateMusic_NullPrompt() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> sunoApiClient.generateMusic(null));
        assertEquals("提示词不能为空", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }

    @Test
    void generateMusic_EmptyPrompt() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> sunoApiClient.generateMusic(""));
        assertEquals("提示词不能为空", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getMusicStatus_Success() {
        // Given
        String musicId = "test-id";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", COOKIE);
        HttpEntity<?> request = new HttpEntity<>(headers);
        
        Map<String, Object> responseBody = Map.of(
            "status", "completed",
            "url", "test-url"
        );
        ResponseEntity<Map<String, Object>> response = ResponseEntity.ok(responseBody);
        
        when(restTemplate.exchange(
            eq(API_URL + "/get?ids=" + musicId),
            eq(HttpMethod.GET),
            argThat(req -> req.getHeaders().equals(headers)),
            any(ParameterizedTypeReference.class)
        )).thenReturn(response);

        // When
        Map<String, Object> result = sunoApiClient.getMusicStatus(musicId);

        // Then
        assertEquals(responseBody, result);
        verify(restTemplate).exchange(
            eq(API_URL + "/get?ids=" + musicId),
            eq(HttpMethod.GET),
            argThat(req -> req.getHeaders().equals(headers)),
            any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void getMusicStatus_NullId() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> sunoApiClient.getMusicStatus(null));
        assertEquals("音乐ID不能为空", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getMusicStatus_EmptyId() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> sunoApiClient.getMusicStatus(""));
        assertEquals("音乐ID不能为空", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getMusicStatus_Failed() {
        // Given
        String musicId = "test-id";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", COOKIE);
        HttpEntity<?> request = new HttpEntity<>(headers);
        
        Map<String, Object> responseBody = Map.of("status", "failed");
        ResponseEntity<Map<String, Object>> response = ResponseEntity.ok(responseBody);
        
        when(restTemplate.exchange(
            eq(API_URL + "/get?ids=" + musicId),
            eq(HttpMethod.GET),
            argThat(req -> req.getHeaders().equals(headers)),
            any(ParameterizedTypeReference.class)
        )).thenReturn(response);

        // When
        Map<String, Object> result = sunoApiClient.getMusicStatus(musicId);

        // Then
        assertEquals(responseBody, result);
    }

    @Test
    void getMusicStatus_Processing() {
        // Given
        String musicId = "test-id";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", COOKIE);
        HttpEntity<?> request = new HttpEntity<>(headers);
        
        Map<String, Object> responseBody = Map.of("status", "processing");
        ResponseEntity<Map<String, Object>> response = ResponseEntity.ok(responseBody);
        
        when(restTemplate.exchange(
            eq(API_URL + "/get?ids=" + musicId),
            eq(HttpMethod.GET),
            argThat(req -> req.getHeaders().equals(headers)),
            any(ParameterizedTypeReference.class)
        )).thenReturn(response);

        // When
        Map<String, Object> result = sunoApiClient.getMusicStatus(musicId);

        // Then
        assertEquals(responseBody, result);
    }

    @Test
    void getMusicStatus_NotFound() {
        // Given
        String musicId = "non-existent-id";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", COOKIE);
        HttpEntity<?> request = new HttpEntity<>(headers);
        
        when(restTemplate.exchange(
            eq(API_URL + "/get?ids=" + musicId),
            eq(HttpMethod.GET),
            argThat(req -> req.getHeaders().equals(headers)),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new RestClientException("Not Found"));

        // When & Then
        ExternalApiException exception = assertThrows(ExternalApiException.class,
            () -> sunoApiClient.getMusicStatus(musicId));
        assertEquals("API调用失败: Not Found", exception.getMessage());
    }

    @Test
    void getQuota_Success() {
        // Given
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("remaining_credits", 100);
        mockResponse.put("total_credits", 1000);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", COOKIE);
        HttpEntity<?> request = new HttpEntity<>(headers);
        
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(
            eq(API_URL + "/quota"),
            eq(HttpMethod.GET),
            argThat(req -> req.getHeaders().equals(headers)),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);
        
        // When
        Map<String, Object> result = sunoApiClient.getQuota();
        
        // Then
        assertNotNull(result);
        assertEquals(100, result.get("remaining_credits"));
        assertEquals(1000, result.get("total_credits"));
        
        verify(restTemplate).exchange(
            eq(API_URL + "/quota"),
            eq(HttpMethod.GET),
            argThat(req -> req.getHeaders().equals(headers)),
            any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void apiError_ThrowsExternalApiException() {
        // Given
        String prompt = "test prompt";
        when(restTemplate.exchange(
            eq(API_URL + "/generate"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new RestClientException("API Error"));

        // When & Then
        ExternalApiException exception = assertThrows(ExternalApiException.class,
            () -> sunoApiClient.generateMusic(prompt));
        assertEquals("API调用失败: API Error", exception.getMessage());
    }

    @Test
    void apiError_NullResponse() {
        // Given
        String prompt = "test prompt";
        when(restTemplate.exchange(
            eq(API_URL + "/generate"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(null));

        // When & Then
        ExternalApiException exception = assertThrows(ExternalApiException.class,
            () -> sunoApiClient.generateMusic(prompt));
        assertEquals("API响应为空", exception.getMessage());
    }
} 