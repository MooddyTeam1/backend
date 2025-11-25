package com.moa.backend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 응답")
public record SignUpResponse(
    @Schema(description = "사용자 ID", example = "1")
    Long id,
    @Schema(description = "이메일", example = "user@example.com")
    String email,
    @Schema(description = "이름", example = "홍길동")
    String name
) {
}
