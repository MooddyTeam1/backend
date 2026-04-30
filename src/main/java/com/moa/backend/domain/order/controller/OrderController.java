package com.moa.backend.domain.order.controller;

import com.moa.backend.domain.order.dto.OrderCreateRequest;
import com.moa.backend.domain.order.dto.OrderDetailResponse;
import com.moa.backend.domain.order.dto.OrderPageResponse;
import com.moa.backend.domain.order.service.OrderRedisFacade;
import com.moa.backend.global.error.ErrorResponse;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    private final OrderRedisFacade orderRedisFacade;

    /**
     * 서포터가 주문을 신규 생성한다.
     */
    @PostMapping
    @Operation(summary = "주문 생성", description = "로그인한 서포터가 리워드/배송지 정보를 포함해 주문을 생성합니다. 재고가 부족한 리워드는 주문할 수 없고, 프로젝트가 펀딩 가능 상태가 아닐 경우 생성이 거부됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "주문 생성 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패 또는 수량/금액 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "프로젝트 또는 리워드를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "재고 부족 또는 비즈니스 상태 충돌", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<OrderDetailResponse> createOrder(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Valid @RequestBody OrderCreateRequest request
    ) {
        OrderDetailResponse response = orderRedisFacade.createOrder(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 주문 상세 정보를 조회한다.
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "로그인 사용자가 자신의 주문 상세 정보를 조회합니다. 본인 소유 주문만 조회 가능하며 타인 주문 접근은 거부됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 상세 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "주문 조회 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<OrderDetailResponse> getOrder(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(description = "조회할 주문 ID", example = "1400") @PathVariable Long orderId
    ) {
        OrderDetailResponse response = orderRedisFacade.getOrder(principal.getId(), orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그인 사용자의 전체 주문 목록(요약)을 조회한다.
     */
    @GetMapping
    @Operation(summary = "주문 목록 조회", description = "로그인 사용자의 주문 목록을 페이지로 조회합니다. page는 0부터 시작하며 size는 과도한 값으로 요청할 수 없습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "페이지 파라미터가 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<OrderPageResponse> getOrders(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(description = "페이지 번호(0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기(기본 10, 권장 최대 100)", example = "10") @RequestParam(defaultValue = "10") int size
    ) {
        OrderPageResponse response = orderRedisFacade.getOrders(principal.getId(), page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그인 사용자가 자신의 주문을 취소한다.
     */
    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "주문 취소", description = "로그인 사용자가 자신의 주문을 취소합니다. 이미 결제가 완료(PAID)된 주문은 취소할 수 없습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 취소 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "주문 취소 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 결제 완료되어 취소 불가 등 비즈니스 충돌", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> cancelOrder(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(description = "취소할 주문 ID", example = "1400") @PathVariable Long orderId,
            @Parameter(description = "취소 사유(운영 로그 기록용)", example = "색상이 예상과 달라 주문 취소") @RequestParam(required = false, defaultValue = "사용자 취소") String reason
    ) {
        orderRedisFacade.cancelOrder(principal.getId(), orderId, reason);
        return ResponseEntity.ok().build();
    }
}
