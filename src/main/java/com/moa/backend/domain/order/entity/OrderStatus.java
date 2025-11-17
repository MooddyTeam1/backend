package com.moa.backend.domain.order.entity;

/**
 * 주문의 결제 상태.
 */
public enum OrderStatus {

    // 생성 후 결제 대기
    PENDING,

    // 결제 완료
    PAID,

    // 주문 취소
    CANCELED
}
