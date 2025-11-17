package com.moa.backend.external.tosspayments.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TossCancelResponse {
    private String paymentKey;
    private String orderId;
    private String status;              // "CANCELED"
    private LocalDateTime canceledAt;

    private CancelInfo cancels;

    @Getter
    @NoArgsConstructor
    public static class CancelInfo {
        private Long cancelAmount;
        private String cancelReason;
        private LocalDateTime canceledAt;
    }
}
