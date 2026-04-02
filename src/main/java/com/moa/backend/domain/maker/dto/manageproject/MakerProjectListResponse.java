package com.moa.backend.domain.maker.dto.manageproject;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 한글 설명: 메이커 프로젝트 목록 응답 DTO (페이징 포함).
 * - GET /api/maker/projects 응답 전체를 표현한다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "MakerProjectListResponse DTO")
public class MakerProjectListResponse {

    @Schema(description = "projects", example = "[]")

    private List<MakerProjectListItemResponse> projects; // 프로젝트 카드 목록
    @Schema(description = "totalCount", example = "1")
    private Long totalCount;                             // 필터 적용 후 전체 개수
    @Schema(description = "page", example = "1")
    private Integer page;                                // 현재 페이지(1부터 시작)
    @Schema(description = "pageSize", example = "1")
    private Integer pageSize;                            // 페이지 크기
    @Schema(description = "totalPages", example = "1")
    private Integer totalPages;                          // 전체 페이지 수
}
