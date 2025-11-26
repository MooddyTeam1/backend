package com.moa.backend.domain.admin.dto.statistics.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 알림 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "대시보드 알림")
public class AlertDto {

    @Schema(description = "알림 타입", example = "WARNING")
    private String type;      // WARNING, ERROR, INFO
    @Schema(description = "알림 제목", example = "정산 대기 알림")
    private String title;     // 알림 제목
    @Schema(description = "알림 메시지", example = "1201 프로젝트 잔금 정산 대기 중")
    private String message;   // 알림 메시지
}
