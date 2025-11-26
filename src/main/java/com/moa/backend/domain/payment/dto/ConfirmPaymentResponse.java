package com.moa.backend.domain.payment.dto;

import com.moa.backend.domain.payment.entity.Payment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "결제 확정 응답")
public class ConfirmPaymentResponse {
    @Schema(description = "결제 ID", example = "9001")
    private Long paymentId;
    @Schema(description = "주문 코드", example = "ORD-20250105-0001")
    private String orderId;
    @Schema(description = "PG paymentKey", example = "pay_ABCDEFG123456")
    private String paymentKey;
    @Schema(description = "결제 수단", example = "CARD")
    private String method;
    @Schema(description = "결제 금액", example = "29000")
    private Long amount;
    @Schema(description = "결제 상태", example = "PAID")
    private String status;
    @Schema(description = "승인 시각", example = "2025-01-05T12:00:00")
    private LocalDateTime approvedAt;
    @Schema(description = "영수증 URL", example = "https://pay.moa.com/receipt/abc123")
    private String receiptUrl;

    public static ConfirmPaymentResponse from(Payment payment) {
        return ConfirmPaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrder().getOrderCode())
                .paymentKey(payment.getPaymentKey())
                .method(payment.getMethod())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .approvedAt(payment.getApprovedAt())
                .receiptUrl(payment.getReceiptUrl())
                .build();
    }
}
