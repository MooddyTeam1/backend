package com.moa.backend.domain.maker.controller;

import com.moa.backend.domain.maker.dto.manageproject.MakerProjectOrderSummaryResponse;
import com.moa.backend.domain.maker.service.MakerProjectOrderService;
import com.moa.backend.global.dto.PageResponse;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 한글 설명: 메이커 콘솔 - 프로젝트 주문/서포터 리스트 조회 컨트롤러.
 */
@RestController
@RequestMapping("/api/maker/projects/{projectId}/orders")
@RequiredArgsConstructor
@Tag(name = "Maker-Project-Orders", description = "메이커 프로젝트 주문/서포터 리스트")
public class MakerProjectOrderController {

    private final MakerProjectOrderService orderService;

    @GetMapping
    @Operation(summary = "메이커 프로젝트 주문 리스트 조회 (페이징 + 상태 필터)")
    public ResponseEntity<PageResponse<MakerProjectOrderSummaryResponse>> getOrders(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1200") @PathVariable Long projectId,

            @RequestParam(name = "page", defaultValue = "0")
            int page,

            @RequestParam(name = "size", defaultValue = "20")
            int size,

            // 한글 설명: 결제 상태 필터 (예: PAID, CANCELLED 등) - 선택값
            @RequestParam(name = "paymentStatus", required = false)
            String paymentStatus,

            // 한글 설명: 배송 상태 필터 (예: READY, SHIPPED 등) - 선택값
            @RequestParam(name = "deliveryStatus", required = false)
            String deliveryStatus
    ) {
        Long makerUserId = principal.getId();

        PageResponse<MakerProjectOrderSummaryResponse> response =
                orderService.getOrdersForMaker(
                        makerUserId,
                        projectId,
                        page,
                        size,
                        paymentStatus,
                        deliveryStatus
                );

        return ResponseEntity.ok(response);
    }
}
