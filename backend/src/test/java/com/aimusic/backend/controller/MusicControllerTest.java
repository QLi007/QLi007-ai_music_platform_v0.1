package com.aimusic.backend.controller;

import com.aimusic.backend.config.TestConfig;
import com.aimusic.backend.domain.dto.MusicDTO;
import com.aimusic.backend.domain.dto.MusicGenerationRequest;
import com.aimusic.backend.domain.entity.MusicStatus;
import com.aimusic.backend.domain.service.MusicService;
import com.aimusic.backend.exception.EntityNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 音乐控制器测试类
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class MusicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MusicService musicService;

    private MusicDTO mockMusic;
    private MusicGenerationRequest mockRequest;
    private UUID mockId;

    @BeforeEach
    void setUp() {
        mockId = UUID.randomUUID();
        
        // 初始化测试用的DTO
        mockMusic = new MusicDTO();
        mockMusic.setId(mockId);
        mockMusic.setPrompt("测试提示词");
        mockMusic.setStyle("古典");
        mockMusic.setDuration(60);
        mockMusic.setStatus(MusicStatus.PENDING);
        mockMusic.setCreatedAt(LocalDateTime.now());
        mockMusic.setUpdatedAt(LocalDateTime.now());

        // 初始化测试用的请求对象
        mockRequest = new MusicGenerationRequest();
        mockRequest.setPrompt("测试提示词");
        mockRequest.setStyle("古典");
        mockRequest.setDuration(60);
    }

    @Test
    void generateMusic_WithValidRequest_ShouldReturnCreatedMusic() throws Exception {
        when(musicService.generateMusic(any(MusicGenerationRequest.class))).thenReturn(mockMusic);

        mockMvc.perform(post("/api/v1/music/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockId.toString()))
                .andExpect(jsonPath("$.prompt").value(mockRequest.getPrompt()))
                .andExpect(jsonPath("$.style").value(mockRequest.getStyle()))
                .andExpect(jsonPath("$.duration").value(mockRequest.getDuration()))
                .andExpect(jsonPath("$.status").value(MusicStatus.PENDING.toString()));
    }

    @Test
    void generateMusic_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        MusicGenerationRequest invalidRequest = new MusicGenerationRequest();
        invalidRequest.setPrompt("");  // 空提示词
        invalidRequest.setStyle("古典");
        invalidRequest.setDuration(-1); // 负数时长

        mockMvc.perform(post("/api/v1/music/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMusicById_ShouldReturnMusic_WhenMusicExists() throws Exception {
        when(musicService.getMusicById(mockId)).thenReturn(mockMusic);

        mockMvc.perform(get("/api/v1/music/{id}", mockId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockId.toString()))
                .andExpect(jsonPath("$.prompt").value(mockMusic.getPrompt()))
                .andExpect(jsonPath("$.style").value(mockMusic.getStyle()))
                .andExpect(jsonPath("$.duration").value(mockMusic.getDuration()));
    }

    @Test
    void getMusicById_ShouldReturnNotFound_WhenMusicNotExists() throws Exception {
        when(musicService.getMusicById(mockId))
                .thenThrow(new EntityNotFoundException("音乐不存在"));

        mockMvc.perform(get("/api/v1/music/{id}", mockId))
                .andExpect(status().isNotFound());
    }

    @Test
    void listMusic_ShouldReturnPageOfMusic() throws Exception {
        Page<MusicDTO> mockPage = new PageImpl<>(
                Collections.singletonList(mockMusic),
                PageRequest.of(0, 10),
                1
        );
        when(musicService.listMusic(any())).thenReturn(mockPage);

        mockMvc.perform(get("/api/v1/music")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(mockId.toString()))
                .andExpect(jsonPath("$.content[0].prompt").value(mockMusic.getPrompt()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void updateMusicStatus_ShouldUpdateStatus_WhenMusicExists() throws Exception {
        String url = "http://example.com/music.mp3";
        doNothing().when(musicService).updateMusicStatus(mockId, url);

        mockMvc.perform(put("/api/v1/music/{id}/status", mockId)
                .param("url", url))
                .andExpect(status().isOk());

        verify(musicService).updateMusicStatus(mockId, url);
    }

    @Test
    void updateMusicStatus_ShouldReturnNotFound_WhenMusicNotExists() throws Exception {
        String url = "http://example.com/music.mp3";
        doThrow(new EntityNotFoundException("音乐不存在"))
                .when(musicService).updateMusicStatus(mockId, url);

        mockMvc.perform(put("/api/v1/music/{id}/status", mockId)
                .param("url", url))
                .andExpect(status().isNotFound());
    }
} 