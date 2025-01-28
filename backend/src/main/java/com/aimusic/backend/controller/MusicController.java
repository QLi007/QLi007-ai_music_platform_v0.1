package com.aimusic.backend.controller;

import com.aimusic.backend.domain.dto.MusicDTO;
import com.aimusic.backend.domain.dto.MusicGenerationRequest;
import com.aimusic.backend.domain.service.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.UUID;

/**
 * 音乐控制器
 * 处理音乐相关的请求
 */
@Tag(name = "音乐管理", description = "音乐生成和管理相关接口")
@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {
    
    private final MusicService musicService;
    
    /**
     * 生成音乐
     *
     * @param request 音乐生成请求
     * @return 生成的音乐信息
     */
    @Operation(summary = "生成音乐")
    @PostMapping("/generate")
    public ResponseEntity<MusicDTO> generateMusic(@Valid @RequestBody MusicGenerationRequest request) {
        return ResponseEntity.ok(musicService.generateMusic(request));
    }
    
    /**
     * 获取音乐详情
     *
     * @param id 音乐ID
     * @return 音乐详情
     */
    @Operation(summary = "获取音乐详情")
    @GetMapping("/{id}")
    public ResponseEntity<MusicDTO> getMusicById(@PathVariable UUID id) {
        return ResponseEntity.ok(musicService.getMusicById(id));
    }
    
    /**
     * 分页查询音乐列表
     *
     * @param pageable 分页参数
     * @return 音乐列表
     */
    @Operation(summary = "获取音乐列表")
    @GetMapping
    public ResponseEntity<Page<MusicDTO>> listMusic(Pageable pageable) {
        return ResponseEntity.ok(musicService.listMusic(pageable));
    }
} 