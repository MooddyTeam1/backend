package com.mooddy.backend.feature.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank (message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이여야 합니다.")
    private String email;

    @NotBlank (message = "비밀번호를 입력해주세요.")
    private String password;
}
