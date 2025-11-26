package com.moa.backend.domain.maker.service;

import com.moa.backend.domain.maker.dto.manageproject.MakerProjectOrderSummaryResponse;
import com.moa.backend.global.dto.PageResponse;

/**
 * 한글 설명: 메이커 콘솔 - 프로젝트 주문/서포터 리스트 조회 서비스.
 */
public interface MakerProjectOrderService {

    /**
     * 한글 설명:
     *  - 메이커가 콘솔에서 보는 주문/서포터 리스트 API
     *  - 결제 상태 / 배송 상태 필터 + 페이지네이션 지원
     *
     * @param makerUserId    현재 로그인한 메이커 유저 ID (소유권 검증용)
     * @param projectId      프로젝트 ID
     * @param page           페이지 번호 (0-based)
     * @param size           페이지 크기
     * @param paymentStatus  결제 상태 필터 (예: PAID, CANCELLED) - null 이면 전체
     * @param deliveryStatus 배송 상태 필터 (예: READY, SHIPPED) - null 이면 전체
     */
    PageResponse<MakerProjectOrderSummaryResponse> getOrdersForMaker(
            Long makerUserId,
            Long projectId,
            int page,
            int size,
            String paymentStatus,
            String deliveryStatus
    );
}
