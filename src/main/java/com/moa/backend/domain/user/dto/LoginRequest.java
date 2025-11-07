package com.moa.backend.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @Email(message = "올바른 이메일 주소를 입력해주세요.")
    @NotBlank(message = "이메일은 필수 값입니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수 값입니다.")
    String password
) {
    // 로그인 요청 처리 시 getEmail() 호출 누락으로 인한 컴파일 오류 방지
    public String getEmail() {
        return email;
    }
}


