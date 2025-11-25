package com.moa.backend.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "결제 확정 요청")
public class ConfirmPaymentRequest {
    @Schema(description = "PG에서 전달된 paymentKey", example = "pay_ABCDEFG123456")
    private String paymentKey;
    @Schema(description = "주문 코드", example = "ORD-20250105-0001")
    private String orderId;
    @Schema(description = "결제 금액", example = "29000")
    private Long amount;
}
