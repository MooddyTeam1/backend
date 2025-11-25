package com.moa.backend.domain.maker.dto.publicpage;

/**
 * 한글 설명: 메이커 소식(업데이트) DTO.
 */
public record MakerNewsDTO(
        Long newsId,             // 한글 설명: 소식 ID
        String title,            // 한글 설명: 제목
        String content,          // 한글 설명: 내용 (요약 또는 전체)
        String thumbnailUrl,     // 한글 설명: 썸네일 이미지 URL (nullable)
        String createdAt,        // 한글 설명: 작성일 (ISO 8601)
        Long projectId,          // 한글 설명: 관련 프로젝트 ID (nullable)
        String projectTitle      // 한글 설명: 관련 프로젝트 제목 (nullable)
) {
}
