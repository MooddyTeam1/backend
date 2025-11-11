package com.moa.backend.domain.wallet.entity;

/**
 * 프로젝트 지갑의 운용 상태를 나타낸다.
 */
public enum ProjectWalletStatus {

    // 정상 사용 중
    ACTIVE,

    // 이상 징후로 잠금 처리
    LOCKED,

    // 프로젝트 종료로 더 이상 사용하지 않음
    CLOSED
}
