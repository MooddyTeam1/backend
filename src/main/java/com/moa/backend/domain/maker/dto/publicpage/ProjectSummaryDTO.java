package com.moa.backend.domain.maker.dto.publicpage;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 한글 설명: 메이커 프로젝트 요약 DTO.
 * - 프론트 메이커 페이지의 프로젝트 카드에 사용.
 */
@Schema(description = "메이커 프로젝트 요약 DTO")
public record ProjectSummaryDTO(
        @Schema(description = "프로젝트 ID", example = "1200")
        Long projectId,            // 한글 설명: 프로젝트 ID
        @Schema(description = "프로젝트 제목", example = "친환경 텀블러 프로젝트")
        String title,              // 한글 설명: 프로젝트 제목
        @Schema(description = "썸네일 URL", example = "https://cdn.moa.com/projects/1200/thumb.jpg")
        String thumbnailUrl,       // 한글 설명: 썸네일 이미지 URL
        @Schema(description = "현재 모금액(원)", example = "3720000")
        Long currentAmount,        // 한글 설명: 현재 모금액
        @Schema(description = "목표 금액(원)", example = "5000000")
        Long goalAmount,           // 한글 설명: 목표 금액
        @Schema(description = "진행률(%)", example = "74")
        Integer progressRate,      // 한글 설명: 진행률 (0~100)
        @Schema(description = "상태 (SCHEDULED, LIVE, ENDED)", example = "LIVE")
        String status,             // 한글 설명: 상태 (LIVE, ENDED, SCHEDULED 등)
        @Schema(description = "종료일(ISO-8601 문자열)", example = "2025-11-30")
        String endDate,            // 한글 설명: 종료일 (ISO 8601 문자열)
        @Schema(description = "남은 일수", example = "12")
        Integer daysLeft           // 한글 설명: 남은 일수 (종료 시 null)
) {
}
