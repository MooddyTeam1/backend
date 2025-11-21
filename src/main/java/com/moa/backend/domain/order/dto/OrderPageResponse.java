package com.moa.backend.domain.order.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 주문 목록 페이징 응답.
 */
@Getter
@Builder
public class OrderPageResponse {

    // 페이지 콘텐츠
    private final List<OrderSummaryResponse> content;
    // 현재 페이지 (0부터 시작)
    private final int page;
    // 페이지 크기
    private final int size;
    // 전체 건수
    private final long totalElements;
    // 전체 페이지 수
    private final int totalPages;

    /**
     * 주문 Page 엔티티를 응답으로 변환한다.
     */
    public static OrderPageResponse from(Page<?> orderPage, List<OrderSummaryResponse> summaries) {
        return OrderPageResponse.builder()
                .content(summaries)
                .page(orderPage.getNumber())
                .size(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .build();
    }

    public static OrderPageResponse fromOrderPage(Page<com.moa.backend.domain.order.entity.Order> orderPage) {
        List<OrderSummaryResponse> summaries = orderPage.getContent().stream()
                .map(OrderSummaryResponse::from)
                .collect(Collectors.toList());
        return from(orderPage, summaries);
    }
}
