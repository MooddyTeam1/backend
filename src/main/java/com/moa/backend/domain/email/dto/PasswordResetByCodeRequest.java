// 한글 설명: 이메일 + 인증코드 + 새 비밀번호를 한 번에 받는 DTO
package com.moa.backend.domain.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordResetByCodeRequest {

    @Email
    @NotBlank
    private String email;       // 한글 설명: 유저 이메일

    @NotBlank
    private String code;        // 한글 설명: 이메일로 받은 6자리 인증코드

    @NotBlank
    private String newPassword; // 한글 설명: 새 비밀번호
}
