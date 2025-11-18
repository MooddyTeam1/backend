package com.moa.backend.domain.order.entity;

/**
 * 주문 배송 진행 상태.
 */
public enum DeliveryStatus {

    //기본상태
    NONE,

    //배송 준비중
    PREPARING,

    // 출고 후 배송 중
    SHIPPING,

    // 배송 완료
    DELIVERED,

    // 구매자가 배송 완료를 확정
    CONFIRMED
}
