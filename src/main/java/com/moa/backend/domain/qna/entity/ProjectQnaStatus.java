package com.moa.backend.domain.qna.entity;

/**
 * 한글 설명: 프로젝트 Q&A 상태
 * - PENDING  : 질문만 등록되고 아직 답변 안 된 상태
 * - ANSWERED : 메이커가 답변한 상태
 */
public enum ProjectQnaStatus {
    PENDING,
    ANSWERED
}
