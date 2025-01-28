package com.aimusic.backend.controller;

import com.aimusic.backend.domain.dto.MusicDTO;
import com.aimusic.backend.domain.dto.MusicGenerationRequest;
import com.aimusic.backend.domain.entity.MusicStatus;
import com.aimusic.backend.domain.entity.User;
import com.aimusic.backend.domain.service.MusicGenerationService;
import com.aimusic.backend.domain.service.MusicService;
import com.aimusic.backend.exception.EntityNotFoundException;
import com.aimusic.backend.utils.MusicTestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 音乐控制器测试类
 */
@WebMvcTest(MusicController.class)
class MusicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MusicService musicService;

    @MockBean
    private MusicGenerationService musicGenerationService;

    private User mockUser;
    private MusicDTO mockMusicDTO;
    private MusicGenerationRequest mockRequest;
    private UUID mockId;

    @BeforeEach
    void setUp() {
        mockUser = MusicTestFactory.createTestUser();
        mockMusicDTO = MusicTestFactory.createTestMusicDTO(mockUser.getId());
        mockRequest = MusicTestFactory.createMusicGenerationRequest(mockUser.getId());
        mockId = mockMusicDTO.getId();
    }

    @Test
    void generateMusic_ShouldReturnCreatedMusic() throws Exception {
        when(musicGenerationService.generateMusic(any(MusicGenerationRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(mockMusicDTO));

        mockMvc.perform(post("/api/v1/music")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockMusicDTO.getId().toString()))
                .andExpect(jsonPath("$.prompt").value(mockMusicDTO.getPrompt()))
                .andExpect(jsonPath("$.status").value(MusicStatus.PENDING.name()));
    }

    @Test
    void getMusicById_ShouldReturnMusic() throws Exception {
        when(musicService.getMusicById(any(UUID.class))).thenReturn(mockMusicDTO);

        mockMvc.perform(get("/api/v1/music/{id}", mockMusicDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockMusicDTO.getId().toString()))
                .andExpect(jsonPath("$.prompt").value(mockMusicDTO.getPrompt()));
    }

    @Test
    void listMusic_ShouldReturnPageOfMusic() throws Exception {
        Page<MusicDTO> musicPage = new PageImpl<>(Collections.singletonList(mockMusicDTO));
        when(musicService.listMusic(any())).thenReturn(musicPage);

        mockMvc.perform(get("/api/v1/music"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(mockMusicDTO.getId().toString()))
                .andExpect(jsonPath("$.content[0].prompt").value(mockMusicDTO.getPrompt()));
    }

    @Test
    void deleteMusic_ShouldReturnNoContent() throws Exception {
        doNothing().when(musicService).deleteMusic(any(UUID.class));
        
        mockMvc.perform(delete("/api/v1/music/{id}", mockMusicDTO.getId()))
                .andExpect(status().isNoContent());
        
        verify(musicService).deleteMusic(mockMusicDTO.getId());
    }

    @Test
    void shouldReturn404_whenMusicNotFound() throws Exception {
        when(musicService.getMusicById(any(UUID.class)))
                .thenThrow(new EntityNotFoundException("Music not found"));

        mockMvc.perform(get("/api/v1/music/{id}", mockId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404_whenDeletingNonExistentMusic() throws Exception {
        doThrow(new EntityNotFoundException("Music not found"))
                .when(musicService).deleteMusic(any(UUID.class));

        mockMvc.perform(delete("/api/v1/music/{id}", mockId))
                .andExpect(status().isNotFound());
    }
} 