package com.moa.backend.domain.settlement.entity;

/**
 * 선지급/잔금 각각의 지급 상태.
 */
public enum SettlementPayoutStatus {

    // 지급 준비 중
    PENDING,

    // 지급 완료
    DONE,

    // 지급 실패
    FAILED
}
