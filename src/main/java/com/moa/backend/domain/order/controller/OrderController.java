package com.moa.backend.domain.order.controller;

import com.moa.backend.domain.order.dto.OrderCreateRequest;
import com.moa.backend.domain.order.dto.OrderDetailResponse;
import com.moa.backend.domain.order.dto.OrderPageResponse;
import com.moa.backend.domain.order.service.OrderService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 주문 생성 및 조회 API를 제공한다.
 * 로그인한 서포터 ID를 기반으로 자신 소유 주문만 접근 가능하다.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "주문 생성/조회/취소 API")
public class OrderController {

    private final OrderService orderService;

    /**
     * 서포터가 주문을 신규 생성한다.
     */
    @PostMapping
    @Operation(summary = "주문 생성", description = "로그인한 서포터가 리워드/배송지 정보를 포함해 주문을 생성합니다.")
    public ResponseEntity<OrderDetailResponse> createOrder(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Valid @RequestBody OrderCreateRequest request
    ) {
        OrderDetailResponse response = orderService.createOrder(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 주문 상세 정보를 조회한다.
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "로그인 사용자가 자신의 주문 상세 정보를 조회합니다.")
    public ResponseEntity<OrderDetailResponse> getOrder(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1400") @PathVariable Long orderId
    ) {
        OrderDetailResponse response = orderService.getOrder(principal.getId(), orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그인 사용자의 전체 주문 목록(요약)을 조회한다.
     */
    @GetMapping
    @Operation(summary = "주문 목록 조회", description = "로그인 사용자의 주문 목록을 페이지로 조회합니다.")
    public ResponseEntity<OrderPageResponse> getOrders(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        OrderPageResponse response = orderService.getOrders(principal.getId(), page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그인 사용자가 자신의 주문을 취소한다.
     */
    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "주문 취소", description = "로그인 사용자가 자신의 주문을 취소합니다.")
    public ResponseEntity<Void> cancelOrder(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1400") @PathVariable Long orderId,
            @RequestParam(required = false, defaultValue = "사용자 취소") String reason
    ) {
        orderService.cancelOrder(principal.getId(), orderId, reason);
        return ResponseEntity.ok().build();
    }
}
