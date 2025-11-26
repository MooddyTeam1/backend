package com.moa.backend.domain.order.entity;

/**
 * 한글 설명: 주문의 배송 진행 상태.
 *
 * - NONE       : 아직 배송 준비/스케줄 미지정 상태
 * - PREPARING  : 포장/출고를 준비 중인 상태
 * - SHIPPING   : 출고되어 택배 이동 중인 상태
 * - DELIVERED  : 플랫폼/메이커 기준 배송 완료 처리된 상태
 * - CONFIRMED  : 서포터가 직접 '수령 완료'를 눌러 확정한 상태
 * - ISSUE      : 반송/주소 오류/분실 등 배송에 문제가 발생한 상태
 */
public enum DeliveryStatus {

    NONE,
    PREPARING,
    SHIPPING,
    DELIVERED,
    CONFIRMED,
    ISSUE
}
