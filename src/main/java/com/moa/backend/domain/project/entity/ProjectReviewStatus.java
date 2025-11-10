package com.moa.backend.domain.project.entity;

// 심사/승인 상태
public enum ProjectReviewStatus {
    NONE,       //심사 요청 안함
    REVIEW,     //심사 요청함 (=심사중)
    APPROVED,   //승인됨
    REJECTED,   //반려됨
}
