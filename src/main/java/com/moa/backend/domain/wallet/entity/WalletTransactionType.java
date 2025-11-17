package com.moa.backend.domain.wallet.entity;

/**
 * 메이커 지갑의 거래 유형.
 * 어떤 이벤트로 잔액 변화가 발생했는지 명시한다.
 */
public enum WalletTransactionType {

    // 외부 입금(선지급 포함)으로 가용 잔액 증가
    DEPOSIT,

    // 메이커 출금으로 가용 잔액 감소
    WITHDRAW,

    // 선지급 정산 금액 적립
    SETTLEMENT_FIRST,
    // 잔금 정산 금액 적립
    SETTLEMENT_FINAL,

    // 환불로 인해 회수된 금액
    REFUND_DEBIT,

    // 운영자 수동 조정
    ADJUSTMENT
}

