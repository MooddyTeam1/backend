package com.moa.backend.domain.maker.dto.manageproject;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한글 설명: 프로젝트 정산 정보 DTO.
 * - 예상 정산액, 수수료, 계좌 정보 등을 담는다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectSettlementResponse {

    // 한글 설명: 총 모금액 (원)
    private Long totalRaised;

    // 한글 설명: 플랫폼 수수료 (원)
    private Long platformFee;

    // 한글 설명: PG 수수료 (원)
    private Long pgFee;

    // 한글 설명: 기타 비용 (원)
    private Long otherFees;

    // 한글 설명: 최종 정산 예상액 (원)
    private Long finalAmount;

    // 한글 설명: 결제 확정일 (없을 수 있음)
    private LocalDateTime paymentConfirmedAt;

    // 한글 설명: 정산 예정일 (없을 수 있음)
    private LocalDateTime settlementScheduledAt;

    // 한글 설명: 정산 계좌 은행명
    private String bankName;

    // 한글 설명: 정산 계좌 번호
    private String accountNumber;

    // 한글 설명: 예금주
    private String accountHolder;
}
