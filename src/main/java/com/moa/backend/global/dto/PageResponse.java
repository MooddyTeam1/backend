package com.moa.backend.global.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * 한글 설명: 공통 페이지네이션 응답 DTO.
 * - content: 실제 데이터 목록
 * - page: 현재 페이지 (0-based)
 * - size: 페이지 크기
 * - totalElements: 전체 데이터 개수
 * - totalPages: 전체 페이지 수
 * - last: 마지막 페이지 여부
 */
@Getter
@Builder
public class PageResponse<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean last;

    /**
     * 한글 설명: Spring Data Page<T> → PageResponse<T> 변환 헬퍼 (기존 메서드)
     */
    public static <T> PageResponse<T> of(org.springframework.data.domain.Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    /**
     * 한글 설명: from(...) 이름으로도 사용할 수 있도록 래핑 메서드 추가
     * - 서비스/컨트롤러에서 PageResponse.from(page) 형태로 사용 가능
     */
    public static <T> PageResponse<T> from(org.springframework.data.domain.Page<T> page) {
        return of(page);
    }
}
