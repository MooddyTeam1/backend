package com.moa.backend.domain.maker.dto.manageproject;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한글 설명: 메이커 프로젝트 상세 - 최근 주문 요약 DTO.
 * - recentOrders 배열의 각 요소에 해당.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "MakerProjectOrderSummaryResponse DTO")
public class MakerProjectOrderSummaryResponse {

    // 한글 설명: 주문 ID
    @Schema(description = "orderId", example = "1")
    private Long orderId;

    // 한글 설명: 주문 코드 (ORD-YYYYMMDD-XXXX 형식 등)
    @Schema(description = "orderCode", example = "orderCode")
    private String orderCode;

    // 한글 설명: 서포터 이름/닉네임
    @Schema(description = "supporterName", example = "supporterName")
    private String supporterName;

    // 한글 설명: 서포터 ID
    @Schema(description = "supporterId", example = "1")
    private Long supporterId;

    // 한글 설명: 대표 리워드명 (주문 내 첫 번째 리워드 등)
    @Schema(description = "rewardTitle", example = "rewardTitle")
    private String rewardTitle;

    // 한글 설명: 대표 리워드 ID
    @Schema(description = "rewardId", example = "1")
    private Long rewardId;

    // 한글 설명: 주문 금액 (원)
    @Schema(description = "amount", example = "1")
    private Long amount;

    // 한글 설명: 결제 상태 (SUCCESS, CANCELLED, REFUNDED, PENDING 등)
    @Schema(description = "paymentStatus", example = "paymentStatus")
    private String paymentStatus;

    // 한글 설명: 배송 상태 (PREPARING, SHIPPED, DELIVERED, NONE 등)
    @Schema(description = "deliveryStatus", example = "deliveryStatus")
    private String deliveryStatus;

    // 한글 설명: 주문일시
    @Schema(description = "orderedAt", example = "2025-11-01T10:00:00")
    private LocalDateTime orderedAt;

    // 한글 설명: 결제일시 (없을 수 있음)
    @Schema(description = "paidAt", example = "2025-11-01T10:00:00")
    private LocalDateTime paidAt;
}
