package com.moa.backend.domain.maker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 한글 설명: 메이커 정산 계좌 정보 등록/수정 요청 DTO.
 * - 프론트에서 PUT /api/profile/me/maker/settlement 로 보낼 본문 형식.
 */
public record MakerSettlementRequest(

        // 한글 설명: 은행명 (예: "KB국민은행", "신한은행").
        @NotBlank(message = "은행명은 필수입니다.")
        @Size(max = 50, message = "은행명은 50자 이하여야 합니다.")
        String bankName,

        // 한글 설명: 계좌번호 (숫자 + 하이픈만 허용).
        @NotBlank(message = "계좌번호는 필수입니다.")
        @Size(max = 50, message = "계좌번호는 50자 이하여야 합니다.")
        @Pattern(regexp = "^[0-9-]+$", message = "계좌번호는 숫자와 하이픈(-)만 입력 가능합니다.")
        String accountNumber,

        // 한글 설명: 예금주명.
        @NotBlank(message = "예금주명은 필수입니다.")
        @Size(max = 100, message = "예금주명은 100자 이하여야 합니다.")
        String accountHolder
) {}
