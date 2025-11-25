package com.moa.backend.domain.maker.dto.publicpage;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 한글 설명: 메이커 프로젝트 목록 페이지 응답 DTO.
 */
@Schema(description = "메이커 프로젝트 목록 페이지 응답")
public record MakerProjectsPageResponse(
        @Schema(description = "프로젝트 요약 목록")
        List<ProjectSummaryDTO> content,  // 한글 설명: 프로젝트 요약 정보 목록
        @Schema(description = "현재 페이지(0-base)", example = "0")
        Integer page,                     // 한글 설명: 현재 페이지 번호 (0-base)
        @Schema(description = "페이지 크기", example = "10")
        Integer size,                     // 한글 설명: 페이지 크기
        @Schema(description = "전체 프로젝트 개수", example = "23")
        Long totalElements,               // 한글 설명: 전체 프로젝트 개수
        @Schema(description = "전체 페이지 수", example = "3")
        Integer totalPages                // 한글 설명: 전체 페이지 수
) {
}
