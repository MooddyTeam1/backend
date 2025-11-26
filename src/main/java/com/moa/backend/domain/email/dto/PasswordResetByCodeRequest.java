package com.moa.backend.domain.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한글 설명:
 *  - 이메일 + 인증코드 + 새 비밀번호로 비밀번호를 재설정하는 요청 DTO
 */
@Getter
@NoArgsConstructor
public class PasswordResetByCodeRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String code;         // 한글 설명: 6자리 인증번호

    @NotBlank
    private String newPassword;  // 한글 설명: 새 비밀번호
}
