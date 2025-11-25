package com.moa.backend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 요청")
public record SignUpRequest(
        @Email(message = "올바른 이메일 주소를 입력해주세요.")
        @NotBlank(message = "이메일은 필수 값입니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 값입니다.")
        @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하로 입력해주세요.")
        String password,

        @NotBlank(message = "이름은 필수 값입니다.")
        @Size(max = 50, message = "이름은 50자 이하로 입력해주세요.")
        String name,

        @NotBlank(message = "인증번호는 필수 값입니다.")
        String verificationCode

) {
    // JSON 직렬화 문제 해결용
    public String getEmail() {
        return email;
    }
}
