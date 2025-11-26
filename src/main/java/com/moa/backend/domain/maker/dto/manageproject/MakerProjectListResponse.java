package com.moa.backend.domain.maker.dto.manageproject;

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
public class MakerProjectListResponse {

    private List<MakerProjectListItemResponse> projects; // 프로젝트 카드 목록
    private Long totalCount;                             // 필터 적용 후 전체 개수
    private Integer page;                                // 현재 페이지(1부터 시작)
    private Integer pageSize;                            // 페이지 크기
    private Integer totalPages;                          // 전체 페이지 수
}
