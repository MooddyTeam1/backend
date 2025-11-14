package com.moa.backend.domain.project.entity;

// 날짜기반 자동 업데이트
public enum ProjectLifecycleStatus {
    DRAFT,      //심사 대기
    SCHEDULED,  //공개 예정 (startDate 전)
    LIVE,       //진행중 (startDate 후)
    ENDED,      //종료됨
    CANCELED    //취소함
}

