package com.moa.backend.domain.maker.dto.publicpage;

/**
 * 한글 설명: 메이커 프로젝트 요약 DTO.
 * - 프론트 메이커 페이지의 프로젝트 카드에 사용.
 */
public record ProjectSummaryDTO(
        Long projectId,            // 한글 설명: 프로젝트 ID
        String title,              // 한글 설명: 프로젝트 제목
        String thumbnailUrl,       // 한글 설명: 썸네일 이미지 URL
        Long currentAmount,        // 한글 설명: 현재 모금액
        Long goalAmount,           // 한글 설명: 목표 금액
        Integer progressRate,      // 한글 설명: 진행률 (0~100)
        String status,             // 한글 설명: 상태 (LIVE, ENDED, SCHEDULED 등)
        String endDate,            // 한글 설명: 종료일 (ISO 8601 문자열)
        Integer daysLeft           // 한글 설명: 남은 일수 (종료 시 null)
) {
}
