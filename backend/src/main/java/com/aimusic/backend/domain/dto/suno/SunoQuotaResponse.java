package com.aimusic.backend.domain.dto.suno;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Suno API 配额响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SunoQuotaResponse {
    private Integer remainingCredits;
    private Integer totalCredits;
} 