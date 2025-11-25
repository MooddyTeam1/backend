package com.moa.backend.domain.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한글 설명:
 *  - 비밀번호 재설정 링크 기반 (토큰 + 새 비밀번호) 요청 DTO
 */
@Getter
@NoArgsConstructor
@Schema(description = "비밀번호 재설정 요청")
public class PasswordResetRequest {

    @Schema(description = "재설정 토큰", example = "reset-token-uuid")
    @NotBlank
    private String token;       // 한글 설명: 비밀번호 재설정 토큰 (쿼리 파라미터 또는 바디에서 전달)

    @Schema(description = "새 비밀번호", example = "NewP@ssw0rd123")
    @NotBlank
    private String newPassword; // 한글 설명: 새 비밀번호

}
