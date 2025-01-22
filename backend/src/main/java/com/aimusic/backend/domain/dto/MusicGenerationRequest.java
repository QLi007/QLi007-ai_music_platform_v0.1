package com.aimusic.backend.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 音乐生成请求
 * 包含生成音乐所需的所有参数
 * 
 * @author AI Music Team
 * @version 0.1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicGenerationRequest {
    /**
     * 音乐标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;

    /**
     * 提示词
     * 用于描述想要生成的音乐风格和特点
     */
    @NotBlank(message = "提示词不能为空")
    @Size(min = 5, max = 500, message = "提示词长度必须在5-500字符之间")
    @Pattern(regexp = "^[\\u4E00-\\u9FA5A-Za-z0-9\\s,.!?，。！？]+$", 
            message = "提示词只能包含中文、英文、数字和基本标点符号")
    private String prompt;

    /**
     * 音乐风格
     * 例如：古典、流行、爵士、电子等
     */
    @NotBlank(message = "音乐风格不能为空")
    @Pattern(regexp = "^(Classical|Jazz|Rock|Pop|Electronic)$", message = "不支持的音乐风格")
    private String style;

    /**
     * 音乐时长（秒）
     * 限制在10秒到300秒之间
     */
    @NotNull(message = "音乐时长不能为空")
    @Min(value = 30, message = "音乐时长不能小于30秒")
    @Max(value = 300, message = "音乐时长不能超过300秒")
    private Integer duration;
} 