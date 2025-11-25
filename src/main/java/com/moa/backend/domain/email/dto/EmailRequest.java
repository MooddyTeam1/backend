package com.moa.backend.domain.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "이메일 전송/검증 요청")
public class EmailRequest {

    @Schema(description = "이메일 주소", example = "user@example.com")
    @Email
    @NotBlank
    private String email;

    @Schema(description = "인증 코드(검증 시 사용)", example = "123456")
    private String code;
}
