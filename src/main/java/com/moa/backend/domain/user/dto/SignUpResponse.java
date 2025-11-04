package com.moa.backend.domain.user.dto;

public record SignUpResponse(
    Long id,
    String email,
    String name
) {
}

