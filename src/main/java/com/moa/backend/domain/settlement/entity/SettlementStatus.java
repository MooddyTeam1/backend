package com.moa.backend.domain.settlement.entity;

/**
 * 정산 전체 진행 상태.
 */
public enum SettlementStatus {

    // 정산 생성 후 대기
    PENDING,

    // 선지급 완료, 잔금 대기
    PARTIAL,

    // 잔금까지 완료
    COMPLETED,

    // 정산 실패
    FAILED
}
