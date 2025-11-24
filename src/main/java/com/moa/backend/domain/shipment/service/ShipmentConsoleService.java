package com.moa.backend.domain.shipment.service;

import com.moa.backend.domain.order.entity.DeliveryStatus;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.entity.OrderStatus;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.shipment.dto.*;
import com.moa.backend.domain.shipment.repository.OrderShipmentQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 한글 설명:
 * - 메이커 배송 관리 콘솔 비즈니스 로직 서비스.
 * - Order 엔티티를 기반으로 배송 상태/송장/메모 등을 제어한다.
 * - 인증된 유저(userId)가 해당 프로젝트의 메이커(ownerUserId)인지 검증한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShipmentConsoleService {

    private final OrderRepository orderRepository;
    private final OrderShipmentQueryRepository orderShipmentQueryRepository;
    private final ProjectRepository projectRepository;

    /**
     * 한글 설명:
     * - 상단 요약 카드 영역 데이터를 조회한다.
     * - totalCount는 "해당 프로젝트의 PAID 주문 수"를 의미한다.
     * - ownerUserId: Maker.owner.id 와 매칭되는 users.id
     */
    public ShipmentSummaryResponse getSummary(Long projectId, Long ownerUserId) {
        // 한글 설명: 해당 프로젝트가 로그인한 메이커의 프로젝트인지 검증
        validateProjectOwner(projectId, ownerUserId);

        long total = orderRepository.countByProjectIdAndStatus(projectId, OrderStatus.PAID);
        long preparing = orderRepository.countByProjectIdAndDeliveryStatus(projectId, DeliveryStatus.PREPARING);
        long shipping = orderRepository.countByProjectIdAndDeliveryStatus(projectId, DeliveryStatus.SHIPPING);
        long delivered = orderRepository.countByProjectIdAndDeliveryStatus(projectId, DeliveryStatus.DELIVERED);
        long confirmed = orderRepository.countByProjectIdAndDeliveryStatus(projectId, DeliveryStatus.CONFIRMED);
        long issue = orderRepository.countByProjectIdAndDeliveryStatus(projectId, DeliveryStatus.ISSUE);

        return ShipmentSummaryResponse.builder()
                .totalCount(total)
                .preparingCount(preparing)
                .shippingCount(shipping)
                // UI에서는 delivered + confirmed를 '배송 완료'로 묶어서 사용할 수 있다.
                .deliveredCount(delivered + confirmed)
                .confirmedCount(confirmed)
                .issueCount(issue)
                .build();
    }

    /**
     * 한글 설명:
     * - 배송 목록(리스트 테이블)을 페이징으로 조회한다.
     */
    public ShipmentListResponse getShipments(Long projectId,
                                             Long ownerUserId,
                                             ShipmentSearchCondition condition,
                                             int page,
                                             int pageSize) {

        // 한글 설명: 메이커 권한 검증
        validateProjectOwner(projectId, ownerUserId);

        PageRequest pageable = PageRequest.of(page - 1, pageSize);
        Page<ShipmentListItemResponse> result =
                orderShipmentQueryRepository.searchShipments(projectId, condition, pageable);

        return ShipmentListResponse.builder()
                .items(result.getContent())
                .page(result.getNumber() + 1)
                .pageSize(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();
    }

    // ===================== 상태 변경/송장/메모 =====================

    /**
     * 한글 설명:
     * - 단일 주문의 배송 상태를 변경한다.
     */
    @Transactional
    public void updateDeliveryStatus(Long projectId,
                                     Long ownerUserId,
                                     Long orderId,
                                     UpdateDeliveryStatusRequest request) {

        // 한글 설명: 메이커 권한 검증
        validateProjectOwner(projectId, ownerUserId);

        Order order = findProjectOrder(projectId, orderId);
        DeliveryStatus newStatus = DeliveryStatus.valueOf(request.getStatus());

        if (newStatus == DeliveryStatus.ISSUE) {
            if (request.getIssueReason() == null || request.getIssueReason().isBlank()) {
                throw new IllegalArgumentException("문제/보류 상태로 변경 시 사유가 필요합니다.");
            }
            order.markIssue(request.getIssueReason());
            return;
        }

        switch (newStatus) {
            case PREPARING -> order.setDeliveryStatus(DeliveryStatus.PREPARING);
            case SHIPPING -> order.startDelivery();
            case DELIVERED -> order.completeDelivery();
            case CONFIRMED -> order.confirmBySupporter();
            default -> throw new IllegalArgumentException("지원하지 않는 배송 상태입니다: " + newStatus);
        }
    }

    /**
     * 한글 설명:
     * - 여러 주문의 배송 상태를 한 번에 변경한다.
     * - 체크박스로 선택 후 "배송 준비중으로 변경" 같은 액션에 사용.
     */
    @Transactional
    public void bulkUpdateDeliveryStatus(Long projectId,
                                         Long ownerUserId,
                                         BulkUpdateDeliveryStatusRequest request) {

        // 한글 설명: 메이커 권한 검증
        validateProjectOwner(projectId, ownerUserId);

        DeliveryStatus newStatus = DeliveryStatus.valueOf(request.getStatus());

        List<Order> orders = orderRepository.findAllById(request.getOrderIds());
        for (Order order : orders) {
            // 한글 설명: 다른 프로젝트 주문이면 무시
            if (!order.getProject().getId().equals(projectId)) {
                continue;
            }

            if (newStatus == DeliveryStatus.ISSUE) {
                order.markIssue(request.getIssueReason());
            } else if (newStatus == DeliveryStatus.PREPARING) {
                order.setDeliveryStatus(DeliveryStatus.PREPARING);
            } else if (newStatus == DeliveryStatus.SHIPPING) {
                order.startDelivery();
            } else if (newStatus == DeliveryStatus.DELIVERED) {
                order.completeDelivery();
            } else if (newStatus == DeliveryStatus.CONFIRMED) {
                order.confirmBySupporter();
            }
        }
    }

    /**
     * 한글 설명:
     * - 송장/택배사 정보를 수정한다.
     * - autoStartDelivery=true 인 경우, 송장 입력과 동시에 배송중으로 전환할 수 있다.
     */
    @Transactional
    public void updateTrackingInfo(Long projectId,
                                   Long ownerUserId,
                                   Long orderId,
                                   UpdateTrackingInfoRequest request) {

        // 한글 설명: 메이커 권한 검증
        validateProjectOwner(projectId, ownerUserId);

        Order order = findProjectOrder(projectId, orderId);
        order.updateTrackingInfo(
                request.getCourierName(),
                request.getTrackingNumber(),
                request.isAutoStartDelivery()
        );
    }

    /**
     * 한글 설명:
     * - 메이커 내부용 배송 메모를 수정한다.
     */
    @Transactional
    public void updateDeliveryMemo(Long projectId,
                                   Long ownerUserId,
                                   Long orderId,
                                   UpdateDeliveryMemoRequest request) {

        // 한글 설명: 메이커 권한 검증
        validateProjectOwner(projectId, ownerUserId);

        Order order = findProjectOrder(projectId, orderId);
        order.setDeliveryMemo(request.getMemo());
    }


    // ===================== 내부 헬퍼 =====================

    /**
     * 한글 설명:
     * - 특정 프로젝트에 속한 주문인지 검증 후 Order 를 조회한다.
     */
    private Order findProjectOrder(Long projectId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!order.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException("해당 프로젝트의 주문이 아닙니다.");
        }
        return order;
    }

    /**
     * 한글 설명:
     * - 현재 로그인한 유저(ownerUserId)가 해당 프로젝트의 메이커인지 검증한다.
     * - 구조: Project.maker.owner.id == ownerUserId ?
     */
    private void validateProjectOwner(Long projectId, Long ownerUserId) {
        boolean exists = projectRepository.existsByIdAndMaker_Owner_Id(projectId, ownerUserId);
        if (!exists) {
            throw new IllegalArgumentException("해당 프로젝트에 대한 배송 관리 권한이 없습니다.");
        }
    }
}
