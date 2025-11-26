package com.moa.backend.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한글 설명: 프로젝트 심사 반려 요청 DTO.
 * - reason 필수, 최대 1000자.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로젝트 심사 반려 요청")
public class RejectProjectRequest {
    @Schema(description = "반려 사유", example = "필수 서류 미비")
    @NotBlank(message = "반려 사유는 필수입니다.")
    @Size(max = 1000, message = "반려 사유는 1000자 이하여야 합니다.")
    private String reason;
}
