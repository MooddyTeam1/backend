package com.moa.backend.domain.wallet.entity;

/**
 * 플랫폼 지갑 거래 타입.
 */
public enum PlatformWalletTransactionType {

    //PG 정산 계좌에서 순수 결제 금액 입금
    PAYMENT_DEPOSIT,

    // 메이커에게 송금한 금액
    WITHDRAW_TO_MAKER,

    // 고객 환불을 위해 송금한 금액
    REFUND_OUT,

    // 플랫폼 수수료 수익 반영
    PLATFORM_FEE_IN,

    // PG 수수료 비용 기록
    PG_FEE
}
