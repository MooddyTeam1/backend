package com.moa.backend.domain.maker.dto;

import java.util.List;

/**
 * 한글 설명: 메이커 프로젝트 목록 페이지 응답 DTO.
 */
public record MakerProjectsPageResponse(
        List<ProjectSummaryDTO> content,  // 한글 설명: 프로젝트 요약 정보 목록
        Integer page,                     // 한글 설명: 현재 페이지 번호 (0-base)
        Integer size,                     // 한글 설명: 페이지 크기
        Long totalElements,               // 한글 설명: 전체 프로젝트 개수
        Integer totalPages                // 한글 설명: 전체 페이지 수
) {
}
