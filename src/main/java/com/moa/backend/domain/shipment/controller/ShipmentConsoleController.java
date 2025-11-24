package com.moa.backend.domain.shipment.controller;

import com.moa.backend.domain.shipment.dto.*;
import com.moa.backend.domain.shipment.service.ShipmentConsoleService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 한글 설명:
 * - 메이커 배송 관리 콘솔 API 컨트롤러.
 * - /api/maker/projects/{projectId}/shipments 하위 엔드포인트를 제공한다.
 * - 인증된 메이커의 userId(JwtUserPrincipal.id)를 이용해 권한을 검증한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/maker/projects/{projectId}/shipments")
public class ShipmentConsoleController {

    private final ShipmentConsoleService shipmentConsoleService;

    // 한글 설명: 상단 요약 카드 데이터 조회
    @GetMapping("/summary")
    public ResponseEntity<ShipmentSummaryResponse> getSummary(
            @PathVariable Long projectId,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        Long ownerUserId = principal.getId(); // users.id
        ShipmentSummaryResponse response =
                shipmentConsoleService.getSummary(projectId, ownerUserId);
        return ResponseEntity.ok(response);
    }

    // 한글 설명: 배송 목록 조회
    @GetMapping
    public ResponseEntity<ShipmentListResponse> getShipments(
            @PathVariable Long projectId,
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) Long rewardId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int pageSize
    ) {
        Long ownerUserId = principal.getId();

        ShipmentSearchCondition condition = ShipmentSearchCondition.builder()
                .status("ALL".equalsIgnoreCase(status) ? null : status)
                .rewardId(rewardId)
                .search(search)
                .sortBy(sortBy)
                .sortOrder(sortOrder)
                .build();

        ShipmentListResponse response =
                shipmentConsoleService.getShipments(projectId, ownerUserId, condition, page, pageSize);

        return ResponseEntity.ok(response);
    }

    // 한글 설명: 단일 주문 배송 상태 변경
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long projectId,
            @PathVariable Long orderId,
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestBody UpdateDeliveryStatusRequest request
    ) {
        Long ownerUserId = principal.getId();
        shipmentConsoleService.updateDeliveryStatus(projectId, ownerUserId, orderId, request);
        return ResponseEntity.noContent().build();
    }

    // 한글 설명: 여러 주문 배송 상태 일괄 변경
    @PutMapping("/bulk-status")
    public ResponseEntity<Void> bulkUpdateStatus(
            @PathVariable Long projectId,
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestBody BulkUpdateDeliveryStatusRequest request
    ) {
        Long ownerUserId = principal.getId();
        shipmentConsoleService.bulkUpdateDeliveryStatus(projectId, ownerUserId, request);
        return ResponseEntity.noContent().build();
    }

    // 한글 설명: 송장/택배사 정보 수정
    @PutMapping("/{orderId}/tracking")
    public ResponseEntity<Void> updateTracking(
            @PathVariable Long projectId,
            @PathVariable Long orderId,
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestBody UpdateTrackingInfoRequest request
    ) {
        Long ownerUserId = principal.getId();
        shipmentConsoleService.updateTrackingInfo(projectId, ownerUserId, orderId, request);
        return ResponseEntity.noContent().build();
    }

    // 한글 설명: 배송 메모 수정
    @PutMapping("/{orderId}/memo")
    public ResponseEntity<Void> updateMemo(
            @PathVariable Long projectId,
            @PathVariable Long orderId,
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestBody UpdateDeliveryMemoRequest request
    ) {
        Long ownerUserId = principal.getId();
        shipmentConsoleService.updateDeliveryMemo(projectId, ownerUserId, orderId, request);
        return ResponseEntity.noContent().build();
    }
}
