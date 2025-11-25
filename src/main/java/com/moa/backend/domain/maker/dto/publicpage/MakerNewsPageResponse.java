package com.moa.backend.domain.maker.dto.publicpage;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 한글 설명: 메이커 소식 페이지 응답 DTO.
 */
@Schema(description = "메이커 소식 페이지 응답")
public record MakerNewsPageResponse(
        @Schema(description = "소식 목록")
        List<MakerNewsDTO> content,    // 한글 설명: 소식 목록
        @Schema(description = "현재 페이지(0-base)", example = "0")
        Integer page,
        @Schema(description = "페이지 크기", example = "10")
        Integer size,
        @Schema(description = "전체 건수", example = "20")
        Long totalElements,
        @Schema(description = "전체 페이지 수", example = "2")
        Integer totalPages
) {
}
