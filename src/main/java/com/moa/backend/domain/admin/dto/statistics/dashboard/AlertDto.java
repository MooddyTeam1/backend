package com.moa.backend.domain.admin.dto.statistics.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 알림 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class AlertDto {

    private String type;      // WARNING, ERROR, INFO
    private String title;     // 알림 제목
    private String message;   // 알림 메시지
}
