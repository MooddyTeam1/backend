package com.moa.backend.domain.payment.dto;

import com.moa.backend.domain.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmPaymentResponse {
    private Long paymentId;
    private String orderId;
    private String paymentKey;
    private String method;
    private Long amount;
    private String status;
    private LocalDateTime approvedAt;
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
