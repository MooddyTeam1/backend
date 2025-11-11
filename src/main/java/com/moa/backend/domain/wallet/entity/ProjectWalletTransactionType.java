package com.moa.backend.domain.wallet.entity;

/**
 * 프로젝트 지갑 거래 로그 타입.
 */
public enum ProjectWalletTransactionType {

    // 결제 승인으로 에스크로 입금
    DEPOSIT,

    // 환불로 에스크로 차감
    REFUND,

    // 정산 생성으로 release 대기 금액 증가
    RELEASE_PENDING,

    // 메이커 송금으로 실제 에스크로 차감
    RELEASE
}
