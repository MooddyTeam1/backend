package com.moa.backend.domain.payment.controller;

import com.moa.backend.domain.payment.dto.ConfirmPaymentRequest;
import com.moa.backend.domain.payment.dto.ConfirmPaymentResponse;
import com.moa.backend.domain.payment.entity.Payment;
import com.moa.backend.domain.payment.service.PaymentService;
import com.moa.backend.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "결제 승인/취소")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 승인 API
     * 토스페이먼츠에서 결제 후 successUrl로 리다이렉트될 때
     * 프론트엔드가 이 API를 호출해서 결제를 승인합니다.
     */
    @PostMapping("/confirm")
    @Operation(summary = "결제 승인", description = "PG 결제 성공 후 결제 승인 처리를 수행합니다. 요청 금액은 주문 금액과 반드시 일치해야 하며, 이미 승인된 결제는 중복 승인할 수 없습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 승인 성공"),
            @ApiResponse(responseCode = "400", description = "요청 금액 불일치 또는 유효성 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "주문 또는 결제를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 승인된 결제", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ConfirmPaymentResponse> confirmPayment(
            @RequestBody ConfirmPaymentRequest request
    ) {
        Payment payment = paymentService.confirmPayment(
                request.getPaymentKey(),
                request.getOrderId(),
                request.getAmount()
        );

        ConfirmPaymentResponse response = ConfirmPaymentResponse.from(payment);
        return ResponseEntity.ok(response);
    }

    /**
     * 결제 취소 API
     * 관리자 또는 사용자가 결제를 취소할 때 사용합니다.
     */
    @PostMapping("/{paymentId}/cancel")
    @Operation(summary = "결제 취소", description = "승인된 결제를 취소합니다. 이미 취소 완료된 결제 또는 취소 불가 상태의 결제는 처리할 수 없습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 취소 성공"),
            @ApiResponse(responseCode = "400", description = "취소 요청이 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "결제를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 처리된 결제 또는 상태 충돌", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> cancelPayment(
            @Parameter(description = "취소할 결제 ID", example = "1500") @PathVariable Long paymentId,
            @Parameter(description = "결제 취소 사유(정산/CS 이력 용도)", example = "중복 결제 발생으로 취소") @RequestParam(required = false, defaultValue = "사용자 요청") String reason
    ) {
        paymentService.cancelPayment(paymentId, reason);
        return ResponseEntity.ok().build();
    }
}
