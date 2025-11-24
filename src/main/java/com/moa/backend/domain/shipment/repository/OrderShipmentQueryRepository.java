package com.moa.backend.domain.shipment.repository;

import com.moa.backend.domain.shipment.dto.ShipmentListItemResponse;
import com.moa.backend.domain.shipment.dto.ShipmentSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 한글 설명:
 * - 메이커 배송 콘솔 리스트 조회용 커스텀 리포지토리.
 * - Order + OrderItem + User 등을 조인하여 DTO로 바로 조회한다.
 */
public interface OrderShipmentQueryRepository {

    Page<ShipmentListItemResponse> searchShipments(Long projectId,
                                                   ShipmentSearchCondition condition,
                                                   Pageable pageable);
}
