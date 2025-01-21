package com.aimusic.backend.controller;

import com.aimusic.backend.domain.dto.MusicDTO;
import com.aimusic.backend.domain.dto.MusicGenerationRequest;
import com.aimusic.backend.domain.service.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 音乐控制器
 * 处理音乐生成、查询、状态更新等请求
 * 
 * @author AI Music Team
 * @version 0.1.0
 */
@Tag(name = "音乐生成", description = "音乐生成相关接口，包括音乐生成、查询、状态更新等功能")
@RestController
@RequestMapping("/api/v1/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    /**
     * 生成音乐
     *
     * @param request 音乐生成请求
     * @return 生成的音乐信息
     */
    @Operation(
        summary = "生成音乐",
        description = "根据提供的参数生成一段音乐，包括提示词、风格和时长等"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "音乐生成成功",
            content = @Content(schema = @Schema(implementation = MusicDTO.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/generate")
    public ResponseEntity<MusicDTO> generateMusic(
            @Valid @RequestBody MusicGenerationRequest request) {
        MusicDTO music = musicService.generateMusic(request);
        return ResponseEntity.ok(music);
    }

    /**
     * 获取音乐详情
     *
     * @param id 音乐ID
     * @return 音乐详情
     */
    @Operation(
        summary = "获取音乐详情",
        description = "根据音乐ID获取音乐的详细信息"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功获取音乐信息",
            content = @Content(schema = @Schema(implementation = MusicDTO.class))),
        @ApiResponse(responseCode = "404", description = "音乐不存在")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MusicDTO> getMusicById(
            @Parameter(description = "音乐ID", required = true) 
            @PathVariable UUID id) {
        return ResponseEntity.ok(musicService.getMusicById(id));
    }

    /**
     * 分页查询音乐列表
     *
     * @param pageable 分页参数
     * @return 音乐列表
     */
    @Operation(
        summary = "分页查询音乐列表",
        description = "分页获取音乐列表，支持排序和分页参数"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功获取音乐列表"),
        @ApiResponse(responseCode = "400", description = "分页参数无效")
    })
    @GetMapping
    public ResponseEntity<Page<MusicDTO>> listMusic(
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(musicService.listMusic(pageable));
    }

    /**
     * 更新音乐状态
     *
     * @param id 音乐ID
     * @param url 音乐URL
     */
    @Operation(
        summary = "更新音乐状态",
        description = "更新指定音乐的状态和URL，通常在音乐生成完成后调用"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "状态更新成功"),
        @ApiResponse(responseCode = "404", description = "音乐不存在"),
        @ApiResponse(responseCode = "400", description = "URL参数无效")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateMusicStatus(
            @Parameter(description = "音乐ID", required = true) 
            @PathVariable UUID id,
            @Parameter(description = "音乐文件的URL", required = true) 
            @RequestParam String url) {
        musicService.updateMusicStatus(id, url);
        return ResponseEntity.ok().build();
    }
} 