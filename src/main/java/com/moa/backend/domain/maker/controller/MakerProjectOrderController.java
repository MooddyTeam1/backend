package com.moa.backend.domain.maker.controller;

import com.moa.backend.domain.maker.dto.manageproject.MakerProjectOrderSummaryResponse;
import com.moa.backend.domain.maker.service.MakerProjectOrderService;
import com.moa.backend.global.dto.PageResponse;
import com.moa.backend.global.error.ErrorResponse;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "메이커 프로젝트 주문 리스트 조회", description = "메이커 본인 프로젝트의 주문 목록을 페이징 조회합니다. page는 0부터 시작하며 상태 필터는 서버 enum 값만 허용됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 리스트 조회 성공"),
            @ApiResponse(responseCode = "400", description = "페이지 또는 상태 파라미터 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "해당 프로젝트 조회 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<MakerProjectOrderSummaryResponse>> getOrders(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(description = "조회할 프로젝트 ID", example = "1200") @PathVariable Long projectId,

            @Parameter(description = "페이지 번호(0부터 시작)", example = "0") @RequestParam(name = "page", defaultValue = "0")
            int page,

            @Parameter(description = "페이지 크기(기본 20, 권장 최대 100)", example = "20") @RequestParam(name = "size", defaultValue = "20")
            int size,

            // 한글 설명: 결제 상태 필터 (예: PAID, CANCELLED 등) - 선택값
            @Parameter(description = "결제 상태 필터 (예: PENDING, PAID, CANCELED)", example = "PAID") @RequestParam(name = "paymentStatus", required = false)
            String paymentStatus,

            // 한글 설명: 배송 상태 필터 (예: READY, SHIPPED 등) - 선택값
            @Parameter(description = "배송 상태 필터 (예: PREPARING, SHIPPED, DELIVERED)", example = "SHIPPED") @RequestParam(name = "deliveryStatus", required = false)
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
