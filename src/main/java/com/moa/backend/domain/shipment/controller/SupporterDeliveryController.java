package com.moa.backend.domain.shipment.controller;

import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 한글 설명:
 * - 서포터가 로그인한 상태에서 '수령 완료'를 누를 때 사용하는 API.
 * - 자신의 주문에 대해서만 배송 확정(DELIVERY CONFIRMED)을 할 수 있다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/supporter/orders")
@Tag(name = "Shipment-Supporter", description = "서포터 배송 확정")
public class SupporterDeliveryController {

    private final OrderRepository orderRepository;

    /**
     * 한글 설명:
     * - 서포터 마이페이지 > 주문 상세 화면에서
     *   "수령 완료" 버튼 클릭 시 호출되는 API.
     *
     * 예: PATCH /api/supporter/orders/{orderId}/delivery/confirm
     */
    @PatchMapping("/{orderId}/delivery/confirm")
    @Operation(summary = "서포터 배송 수령확정")
    public ResponseEntity<Void> confirmDelivery(
            @Parameter(example = "1400") @PathVariable Long orderId
            // , @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        Long userId = 0L; // TODO: 인증 컨텍스트에서 실제 로그인 유저 ID 꺼내기

        // 한글 설명: 해당 유저의 주문이 맞는지 검증 (소유권 체크)
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // 한글 설명: 서포터 수령 확인 도메인 메서드
        order.confirmBySupporter();

        return ResponseEntity.noContent().build();
    }
}
