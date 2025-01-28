package com.aimusic.backend.performance;

import com.aimusic.backend.domain.dto.MusicDTO;
import com.aimusic.backend.domain.dto.MusicGenerationRequest;
import com.aimusic.backend.domain.entity.User;
import com.aimusic.backend.domain.repository.UserRepository;
import com.aimusic.backend.domain.service.MusicService;
import com.aimusic.backend.utils.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 音乐服务性能测试
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MusicServicePerformanceTest {

    @Autowired
    private MusicService musicService;

    @Autowired
    private UserRepository userRepository;

    private static final int CONCURRENT_REQUESTS = 10;
    private static final long ACCEPTABLE_DURATION_MS = 5000;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .username("test-user")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(testUser);
    }

    @Test
    void testConcurrentMusicGeneration() {
        int concurrentRequests = CONCURRENT_REQUESTS;
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentRequests);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (int i = 0; i < concurrentRequests; i++) {
            MusicGenerationRequest request = MusicGenerationRequest.builder()
                    .prompt("测试音乐 " + i)
                    .style("古典")
                    .duration(60)
                    .userId(testUser.getId())
                    .build();
            
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                assertDoesNotThrow(() -> {
                    musicService.generateMusic(request);
                });
            }, executorService);
            
            futures.add(future);
        }
        
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdown();
        
        // 验证结果
        Page<MusicDTO> musicPage = musicService.listMusic(PageRequest.of(0, 20));
        assertEquals(concurrentRequests, musicPage.getTotalElements());
    }

    @Test
    void testMusicGenerationResponseTime() {
        // Arrange
        MusicGenerationRequest request = MusicGenerationRequest.builder()
                .prompt("测试音乐生成")
                .style("流行")
                .duration(60)
                .userId(testUser.getId())
                .build();

        // Act
        long startTime = System.currentTimeMillis();
        assertDoesNotThrow(() -> musicService.generateMusic(request));
        long endTime = System.currentTimeMillis();

        // Assert
        long duration = endTime - startTime;
        assertTrue(duration < ACCEPTABLE_DURATION_MS, 
            String.format("音乐生成响应时间应小于%d毫秒,实际用时%d毫秒", ACCEPTABLE_DURATION_MS, duration));
    }
} 