package com.aimusic.backend.controller;

import com.aimusic.backend.domain.dto.MusicDTO;
import com.aimusic.backend.domain.dto.MusicGenerationRequest;
import com.aimusic.backend.domain.service.MusicService;
import com.aimusic.backend.utils.MusicTestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 音乐控制器测试类
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MusicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MusicService musicService;

    private MusicDTO musicDTO;
    private MusicGenerationRequest request;

    @BeforeEach
    void setUp() {
        musicDTO = MusicTestFactory.createMusicDTO();
        request = MusicTestFactory.createMusicGenerationRequest();
    }

    @Test
    void shouldGenerateMusic_whenValidRequest() throws Exception {
        when(musicService.generateMusic(any(MusicGenerationRequest.class)))
                .thenReturn(musicDTO);

        mockMvc.perform(post("/music/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(musicDTO.getId().toString()))
                .andExpect(jsonPath("$.title").value(musicDTO.getTitle()));
    }

    @Test
    void shouldGetMusic_whenValidId() throws Exception {
        when(musicService.getMusicById(any(UUID.class)))
                .thenReturn(musicDTO);

        mockMvc.perform(get("/music/{id}", musicDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(musicDTO.getId().toString()))
                .andExpect(jsonPath("$.title").value(musicDTO.getTitle()));
    }

    @Test
    void shouldListMusic_whenValidPageRequest() throws Exception {
        Page<MusicDTO> page = new PageImpl<>(List.of(musicDTO), PageRequest.of(0, 10), 1);
        when(musicService.listMusic(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/music"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(musicDTO.getId().toString()))
                .andExpect(jsonPath("$.content[0].title").value(musicDTO.getTitle()));
    }

    @Test
    void shouldUpdateMusicStatus_whenValidRequest() throws Exception {
        String url = "https://example.com/updated.mp3";

        mockMvc.perform(put("/music/{id}/status", musicDTO.getId())
                .param("url", url))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400_whenInvalidRequest() throws Exception {
        request.setDuration(-1); // Invalid duration

        mockMvc.perform(post("/music/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
} 