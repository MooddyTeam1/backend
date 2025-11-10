package com.moa.backend.external.tosspayments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TossConfirmRequest {
    private String paymentKey;
    private String orderId;
    private Long amount;
}
