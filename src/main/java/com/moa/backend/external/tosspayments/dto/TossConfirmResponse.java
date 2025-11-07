package com.moa.backend.external.tosspayments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TossConfirmResponse {
    private String paymentKey;
    private String orderId;

    @JsonProperty("totalAmount")
    private Long totalAmount;

    private String method;          // "카드", "간편결제" 등
    private String status;          // "DONE"
    private LocalDateTime approvedAt;

    // 카드 결제 시
    private CardInfo card;

    // 영수증
    private ReceiptInfo receipt;

    @Getter
    @NoArgsConstructor
    public static class CardInfo {
        private String number;      // 마스킹된 번호 (1234-****-****-5678)
        private String company;     // 카드사
        private String cardType;    // 신용/체크
    }

    @Getter
    @NoArgsConstructor
    public static class ReceiptInfo {
        private String url;         // 영수증 URL
    }
}
