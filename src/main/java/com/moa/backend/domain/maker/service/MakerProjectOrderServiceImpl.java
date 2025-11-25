package com.moa.backend.domain.maker.service;

import com.moa.backend.domain.maker.dto.manageproject.MakerProjectOrderSummaryResponse;
import com.moa.backend.domain.order.entity.DeliveryStatus;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.entity.OrderStatus;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.global.dto.PageResponse;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 한글 설명: 메이커 콘솔 - 프로젝트 주문/서포터 리스트 서비스 구현체.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MakerProjectOrderServiceImpl implements MakerProjectOrderService {

    private final ProjectRepository projectRepository;
    private final OrderRepository orderRepository;

    @Override
    public PageResponse<MakerProjectOrderSummaryResponse> getOrdersForMaker(
            Long makerUserId,
            Long projectId,
            int page,
            int size,
            String paymentStatus,
            String deliveryStatus
    ) {
        // 1) 프로젝트 조회 + 메이커 소유권 검증 -----------------------------
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND, "프로젝트를 찾을 수 없습니다."));

        Long ownerId = project.getMaker().getOwner().getId();
        if (!ownerId.equals(makerUserId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "해당 프로젝트의 주문을 조회할 권한이 없습니다.");
        }

        // 2) 필터 문자열 → Enum 파싱 ----------------------------------------
        OrderStatus paymentEnum = parseOrderStatus(paymentStatus);
        DeliveryStatus deliveryEnum = parseDeliveryStatus(deliveryStatus);

        // 3) 페이징 + 정렬 -----------------------------------------------
        PageRequest pageable = PageRequest.of(page, size);

        Page<Order> orderPage = orderRepository.findOrdersForMakerConsole(
                projectId,
                paymentEnum,
                deliveryEnum,
                pageable
        );

        // 4) Order → MakerProjectOrderSummaryResponse 변환 ----------------
        Page<MakerProjectOrderSummaryResponse> dtoPage = orderPage.map(this::toSummaryResponse);

        // 5) 공통 PageResponse 래핑 ---------------------------------------
        return PageResponse.of(dtoPage);
    }

    // =========================================================
    // 내부 유틸: 문자열 → Enum 파싱 (잘못 오면 필터 미적용)
    // =========================================================

    /**
     * 한글 설명:
     *  - paymentStatus 문자열을 OrderStatus enum으로 변환
     *  - null/빈 문자열/잘못된 값인 경우 null 반환(필터 미적용)
     */
    private OrderStatus parseOrderStatus(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return OrderStatus.valueOf(raw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            // 프론트와의 통신이 꼬여도 서버 터지지 않게, 일단 필터 미적용 처리
            return null;
        }
    }

    /**
     * 한글 설명:
     *  - deliveryStatus 문자열을 DeliveryStatus enum으로 변환
     *  - null/빈 문자열/잘못된 값인 경우 null 반환(필터 미적용)
     */
    private DeliveryStatus parseDeliveryStatus(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return DeliveryStatus.valueOf(raw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 한글 설명:
     *  - Order 엔티티를 메이커 콘솔용 요약 DTO로 변환하는 헬퍼 메서드.
     *  - 상단 요약(recentOrders)에서 사용하던 필드 구성을 그대로 재사용한다.
     */
    private MakerProjectOrderSummaryResponse toSummaryResponse(Order order) {
        // 대표 리워드는 첫 번째 OrderItem 기준
        String rewardTitle = null;
        Long rewardId = null;
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            var firstItem = order.getOrderItems().get(0);
            if (firstItem.getReward() != null) {
                rewardTitle = firstItem.getReward().getName();
                rewardId = firstItem.getReward().getId();
            } else {
                // 스냅샷만 있는 경우
                rewardTitle = firstItem.getRewardName();
            }
        }

        return MakerProjectOrderSummaryResponse.builder()
                .orderId(order.getId())
                .orderCode(order.getOrderCode())
                // TODO: 서포터 닉네임(displayName)으로 바꾸고 싶으면 QnA처럼 SupporterProfile 조회해서 교체 가능
                .supporterName(order.getUser().getName())
                .supporterId(order.getUser().getId())
                .rewardTitle(rewardTitle)
                .rewardId(rewardId)
                .amount(order.getTotalAmount())
                .paymentStatus(order.getStatus().name())
                .deliveryStatus(order.getDeliveryStatus().name())
                .orderedAt(order.getCreatedAt())
                // 별도 paidAt 필드 없으면 createdAt/결제완료시간 필드로 교체
                .paidAt(null)
                .build();
    }
}
