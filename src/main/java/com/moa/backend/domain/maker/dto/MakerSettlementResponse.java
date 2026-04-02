package com.moa.backend.domain.maker.dto;

import com.moa.backend.domain.maker.entity.MakerSettlementProfile;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 한글 설명: 메이커 정산 계좌 정보 응답 DTO.
 * - GET/PUT 응답 바디 형식.
 */
@Schema(description = "메이커 정산 계좌 응답")
public record MakerSettlementResponse(
        @Schema(description = "정산 계좌 ID", example = "88")
        Long id,                 // 정산 계좌 정보 ID
        @Schema(description = "메이커 ID", example = "310")
        Long makerId,            // 메이커 ID
        @Schema(description = "은행명", example = "신한은행")
        String bankName,         // 은행명
        @Schema(description = "계좌번호(운영 정책에 따라 마스킹될 수 있음)", example = "***456789")
        String accountNumber,    // 계좌번호 (실제 운영 시 마스킹 추천)
        @Schema(description = "예금주명", example = "김진현")
        String accountHolder,    // 예금주명
        @Schema(description = "생성 시각", example = "2025-10-01T09:20:00")
        LocalDateTime createdAt, // 생성일시
        @Schema(description = "수정 시각", example = "2025-10-20T17:05:00")
        LocalDateTime updatedAt  // 수정일시
) {

    /**
     * 한글 설명: 엔티티 → 응답 DTO 변환용 팩토리 메서드.
     * - 현재는 계좌번호 전체를 내려주지만,
     *   운영 단계에서는 마스킹(masketAccountNumber)으로 교체하는 것을 추천.
     */
    public static MakerSettlementResponse of(MakerSettlementProfile profile) {
        return new MakerSettlementResponse(
                profile.getId(),
                profile.getMaker().getId(),
                profile.getBankName(),
                profile.getAccountNumber(), // TODO: 보안상 마스킹 처리 고려
                profile.getAccountHolder(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }

    /**
     * 한글 설명: 계좌번호 마스킹 버전 DTO로 변환하는 편의 메서드 (선택).
     * - 예: 123-456-789012 → 123-456-***-012
     */
    public static MakerSettlementResponse ofMasked(MakerSettlementProfile profile) {
        return new MakerSettlementResponse(
                profile.getId(),
                profile.getMaker().getId(),
                profile.getBankName(),
                maskAccountNumber(profile.getAccountNumber()),
                profile.getAccountHolder(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }

    /**
     * 한글 설명: 단순 계좌번호 마스킹 유틸 (필요 시 커스터마이징 가능).
     */
    private static String maskAccountNumber(String raw) {
        if (raw == null || raw.length() < 4) {
            return raw;
        }
        // 너무 정교하게 안 가고 뒤 3자리만 남기고 앞부분 일부 마스킹
        int visibleTail = 3;
        String digitsOnly = raw.replaceAll("[^0-9]", "");
        if (digitsOnly.length() <= visibleTail) {
            return raw;
        }
        String maskedPart = "*".repeat(digitsOnly.length() - visibleTail);
        String visiblePart = digitsOnly.substring(digitsOnly.length() - visibleTail);
        return maskedPart + visiblePart;
    }
}
