package com.moa.backend.domain.notification.dto;

import com.moa.backend.domain.notification.entity.Notification;
import com.moa.backend.domain.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "알림 응답")
public class NotificationResponse {

    @Schema(description = "알림 ID", example = "1")
    private Long id;
    @Schema(description = "제목", example = "배송 준비중")
    private String title;
    @Schema(description = "메시지", example = "주문하신 상품이 곧 출고됩니다.")
    private String message;
    @Schema(description = "알림 타입", example = "SUPPORTER")
    private NotificationType type;
    @Schema(description = "읽음 여부", example = "false")
    private boolean read;
    @Schema(description = "생성 시각", example = "2025-11-25T12:00:00")
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

}
