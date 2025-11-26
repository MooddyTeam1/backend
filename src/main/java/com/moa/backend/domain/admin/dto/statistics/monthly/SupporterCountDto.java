package com.moa.backend.domain.admin.dto.statistics.monthly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "서포터 수 요약")
public class SupporterCountDto {
    @Schema(description = "재방문 서포터 수", example = "320")
    private Long repeatSupporterCount;
    @Schema(description = "신규 서포터 수", example = "150")
    private Long newSupporterCount;
}
