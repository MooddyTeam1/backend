package com.moa.backend.domain.maker.dto.manageproject;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "ProjectSettlementResponse DTO")
public class ProjectSettlementResponse {

    // 한글 설명: 총 모금액 (원)
    @Schema(description = "totalRaised", example = "1")
    private Long totalRaised;

    // 한글 설명: 플랫폼 수수료 (원)
    @Schema(description = "platformFee", example = "1")
    private Long platformFee;

    // 한글 설명: PG 수수료 (원)
    @Schema(description = "pgFee", example = "1")
    private Long pgFee;

    // 한글 설명: 기타 비용 (원)
    @Schema(description = "otherFees", example = "1")
    private Long otherFees;

    // 한글 설명: 최종 정산 예상액 (원)
    @Schema(description = "finalAmount", example = "1")
    private Long finalAmount;

    // 한글 설명: 결제 확정일 (없을 수 있음)
    @Schema(description = "paymentConfirmedAt", example = "2025-11-01T10:00:00")
    private LocalDateTime paymentConfirmedAt;

    // 한글 설명: 정산 예정일 (없을 수 있음)
    @Schema(description = "settlementScheduledAt", example = "2025-11-01T10:00:00")
    private LocalDateTime settlementScheduledAt;

    // 한글 설명: 정산 계좌 은행명
    @Schema(description = "bankName", example = "bankName")
    private String bankName;

    // 한글 설명: 정산 계좌 번호
    @Schema(description = "accountNumber", example = "accountNumber")
    private String accountNumber;

    // 한글 설명: 예금주
    @Schema(description = "accountHolder", example = "accountHolder")
    private String accountHolder;
}
