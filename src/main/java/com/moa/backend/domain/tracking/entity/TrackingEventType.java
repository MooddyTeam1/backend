package com.moa.backend.domain.tracking.entity;

/**
 * 한글 설명: 트래킹 이벤트 타입 정의
 *
 * - PAGE_VIEW            : 사이트 아무 페이지나 방문
 * - PROJECT_VIEW         : 프로젝트 상세 페이지 방문
 * - PROJECT_CARD_VIEW    : 리스트/홈에서 프로젝트 카드가 화면에 노출됨 (impression)
 * - PROJECT_CARD_CLICK   : 프로젝트 카드 클릭
 * - SUPPORT_CLICK        : "후원하기" 버튼 클릭
 * - CHECKOUT_VIEW        : 결제 페이지 진입
 * - ORDER_COMPLETED      : 결제 완료
 * - BOOKMARK_ADDED       : 찜 추가
 * - BOOKMARK_REMOVED     : 찜 해제
 * - SHARE                : 공유(카카오, 링크 복사 등)
 */
public enum TrackingEventType {
    PAGE_VIEW,
    PROJECT_VIEW,
    PROJECT_CARD_VIEW,
    PROJECT_CARD_CLICK,
    SUPPORT_CLICK,
    CHECKOUT_VIEW,
    ORDER_COMPLETED,
    BOOKMARK_ADDED,
    BOOKMARK_REMOVED,
    SHARE
}