package com.moa.backend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 요청")
public record SignUpRequest(
    @Schema(description = "이메일", example = "user@example.com")
    @Email(message = "올바른 이메일 주소를 입력해주세요.")
    @NotBlank(message = "이메일은 필수 값입니다.")
    String email,

    @Schema(description = "비밀번호(8~100자)", example = "P@ssw0rd123")
    @NotBlank(message = "비밀번호는 필수 값입니다.")
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하로 입력해주세요.")
    String password,

    @Schema(description = "이름", example = "홍길동")
    @NotBlank(message = "이름은 필수 값입니다.")
    @Size(max = 50, message = "이름은 50자 이하로 입력해주세요.")
    String name
) {
    // AuthController에서 JSON 직렬화된 DTO를 사용할 때 명시적인 getter가 없어 발생한 컴파일 오류 해결
    public String getEmail() {
        return email;
    }
}

