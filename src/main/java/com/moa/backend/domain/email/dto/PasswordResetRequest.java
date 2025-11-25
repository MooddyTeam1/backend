package com.moa.backend.domain.email.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한글 설명:
 *  - 비밀번호 재설정 링크 기반 (토큰 + 새 비밀번호) 요청 DTO
 */
@Getter
@NoArgsConstructor
public class PasswordResetRequest {

    @NotBlank
    private String token;       // 한글 설명: 비밀번호 재설정 토큰 (쿼리 파라미터 또는 바디에서 전달)

    @NotBlank
    private String newPassword; // 한글 설명: 새 비밀번호
}
