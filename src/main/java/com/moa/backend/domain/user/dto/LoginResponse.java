package com.moa.backend.domain.user.dto;

public record LoginResponse(
    Long userId,
    String email,
    String role,
    String tokenType,
    String accessToken,
    long expiresIn,
    String refreshToken,
    long refreshTokenExpiresIn
) {
}
