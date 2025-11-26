package com.moa.backend.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "주문 목록 페이지 응답")
public class OrderPageResponse {

    // 페이지 콘텐츠
    @Schema(description = "주문 요약 목록")
    private final List<OrderSummaryResponse> content;
    // 현재 페이지 (0부터 시작)
    @Schema(description = "현재 페이지(0-base)", example = "0")
    private final int page;
    // 페이지 크기
    @Schema(description = "페이지 크기", example = "10")
    private final int size;
    // 전체 건수
    @Schema(description = "전체 건수", example = "23")
    private final long totalElements;
    // 전체 페이지 수
    @Schema(description = "전체 페이지 수", example = "3")
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
