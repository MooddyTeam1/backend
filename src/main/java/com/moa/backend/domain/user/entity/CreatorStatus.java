package com.moa.backend.domain.user.entity;

public enum CreatorStatus {
    NONE,       // 기본 (판매자 신청 안함)
    PENDING,    // 승인 대기 중
    APPROVED,   // 승인됨 (CREATOR 권한 부여)
    REJECTED    // 반려됨
}
