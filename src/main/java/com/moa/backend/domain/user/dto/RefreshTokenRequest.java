package com.moa.backend.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    @NotBlank(message = "리프레시 토큰은 필수 값입니다.")
    String refreshToken
) {
}

