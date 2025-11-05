package com.moa.backend.domain.order.controller;

import com.moa.backend.domain.order.dto.request.OrderCreateRequest;
import com.moa.backend.domain.order.dto.response.OrderDetailResponse;
import com.moa.backend.domain.order.dto.response.OrderSummaryResponse;
import com.moa.backend.domain.order.service.OrderService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDetailResponse> createOrder(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Valid @RequestBody OrderCreateRequest request
    ) {
        OrderDetailResponse response = orderService.createOrder(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrder(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long orderId
    ) {
        OrderDetailResponse response = orderService.getOrder(principal.getId(), orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderSummaryResponse>> getOrders(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        List<OrderSummaryResponse> responses = orderService.getOrders(principal.getId());
        return ResponseEntity.ok(responses);
    }
}
