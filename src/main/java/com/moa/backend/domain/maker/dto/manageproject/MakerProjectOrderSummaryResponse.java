package com.moa.backend.domain.maker.dto.manageproject;

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
public class MakerProjectOrderSummaryResponse {

    // 한글 설명: 주문 ID
    private Long orderId;

    // 한글 설명: 주문 코드 (ORD-YYYYMMDD-XXXX 형식 등)
    private String orderCode;

    // 한글 설명: 서포터 이름/닉네임
    private String supporterName;

    // 한글 설명: 서포터 ID
    private Long supporterId;

    // 한글 설명: 대표 리워드명 (주문 내 첫 번째 리워드 등)
    private String rewardTitle;

    // 한글 설명: 대표 리워드 ID
    private Long rewardId;

    // 한글 설명: 주문 금액 (원)
    private Long amount;

    // 한글 설명: 결제 상태 (SUCCESS, CANCELLED, REFUNDED, PENDING 등)
    private String paymentStatus;

    // 한글 설명: 배송 상태 (PREPARING, SHIPPED, DELIVERED, NONE 등)
    private String deliveryStatus;

    // 한글 설명: 주문일시
    private LocalDateTime orderedAt;

    // 한글 설명: 결제일시 (없을 수 있음)
    private LocalDateTime paidAt;
}
