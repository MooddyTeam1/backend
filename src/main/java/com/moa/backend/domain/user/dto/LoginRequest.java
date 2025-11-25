package com.moa.backend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청")
public record LoginRequest(
    @Schema(description = "이메일", example = "admin@test.com")
    @Email(message = "올바른 이메일 주소를 입력해주세요.")
    @NotBlank(message = "이메일은 필수 값입니다.")
    String email,

    @Schema(description = "비밀번호", example = "test1234")
    @NotBlank(message = "비밀번호는 필수 값입니다.")
    String password
) {
    // 로그인 요청 처리 시 getEmail() 호출 누락으로 인한 컴파일 오류 방지
    public String getEmail() {
        return email;
    }
}
