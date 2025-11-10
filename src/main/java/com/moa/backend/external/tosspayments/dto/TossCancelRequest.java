package com.moa.backend.external.tosspayments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TossCancelRequest {
    private String cancelReason;    // 취소 사유
    private Long cancelAmount;      // 부분 취소 시 (null이면 전액)
}
