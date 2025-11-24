package com.moa.backend.domain.maker.dto;

import java.util.List;

/**
 * 한글 설명: 메이커 소식 페이지 응답 DTO.
 */
public record MakerNewsPageResponse(
        List<MakerNewsDTO> content,    // 한글 설명: 소식 목록
        Integer page,
        Integer size,
        Long totalElements,
        Integer totalPages
) {
}
