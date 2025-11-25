package com.moa.backend.domain.maker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 한글 설명: 메이커 프로젝트 목록 페이지네이션 응답 DTO.
 * 명세서의 PageResponseDTO<T> 구조를 그대로 따른다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MakerProjectPageResponse {

    // 한글 설명: 실제 프로젝트 데이터 목록
    private List<MakerProjectResponse> content;

    // 한글 설명: 페이지 정보
    private PageInfo page;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        private int number;         // 현재 페이지 번호 (1부터 시작으로 변환해서 내려줌)
        private int size;           // 페이지 크기
        private long totalElements; // 전체 항목 수
        private int totalPages;     // 전체 페이지 수
        private boolean first;      // 첫 페이지 여부
        private boolean last;       // 마지막 페이지 여부
    }

    // 한글 설명: Spring Data Page -> MakerProjectPageResponse 변환 헬퍼
    public static MakerProjectPageResponse from(Page<MakerProjectResponse> page) {
        return MakerProjectPageResponse.builder()
                .content(page.getContent())
                .page(PageInfo.builder()
                        .number(page.getNumber() + 1) // 0-index → 1-index
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .first(page.isFirst())
                        .last(page.isLast())
                        .build())
                .build();
    }
}
