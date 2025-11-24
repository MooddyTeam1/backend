package com.moa.backend.domain.shipment.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 한글 설명:
 * - 배송 목록 조회 시 사용하는 검색/필터 조건을 담는 DTO.
 * - 컨트롤러에서 쿼리 파라미터를 이 객체로 바인딩해서 서비스/리포지토리에 넘긴다.
 */
@Getter
@Builder
public class ShipmentSearchCondition {

    // READY | PREPARING | SHIPPING | DELIVERED | CONFIRMED | ISSUE | ALL
    private String status;

    // 특정 리워드만 필터링할 때 사용
    private Long rewardId;

    // 주문번호 / 서포터명 / 연락처 / 주소 등 검색
    private String search;

    // orderDate | status | amount | deliveryDate
    private String sortBy;

    // asc | desc
    private String sortOrder;
}
