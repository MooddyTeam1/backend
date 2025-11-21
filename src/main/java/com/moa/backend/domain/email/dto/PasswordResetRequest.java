package com.moa.backend.domain.email.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordResetRequest {

    @NotBlank
    private String token;

    @NotBlank
    private String newPassword;
}