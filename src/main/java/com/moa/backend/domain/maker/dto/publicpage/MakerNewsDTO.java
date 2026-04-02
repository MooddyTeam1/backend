package com.moa.backend.domain.maker.dto.publicpage;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 한글 설명: 메이커 소식(업데이트) DTO.
 */
@Schema(description = "메이커 소식 DTO")
public record MakerNewsDTO(
        @Schema(description = "소식 ID", example = "55")
        Long newsId,             // 한글 설명: 소식 ID
        @Schema(description = "소식 제목", example = "리워드 생산이 시작되었습니다")
        String title,            // 한글 설명: 제목
        @Schema(description = "소식 내용", example = "샘플 검수를 완료했고 본생산에 들어갔습니다.")
        String content,          // 한글 설명: 내용 (요약 또는 전체)
        @Schema(description = "썸네일 URL", example = "https://cdn.moa.com/news/55/thumb.jpg")
        String thumbnailUrl,     // 한글 설명: 썸네일 이미지 URL (nullable)
        @Schema(description = "작성 시각(ISO-8601)", example = "2025-11-01T10:00:00")
        String createdAt,        // 한글 설명: 작성일 (ISO 8601)
        @Schema(description = "연관 프로젝트 ID", example = "1200")
        Long projectId,          // 한글 설명: 관련 프로젝트 ID (nullable)
        @Schema(description = "연관 프로젝트 제목", example = "친환경 텀블러 프로젝트")
        String projectTitle      // 한글 설명: 관련 프로젝트 제목 (nullable)
) {
}
