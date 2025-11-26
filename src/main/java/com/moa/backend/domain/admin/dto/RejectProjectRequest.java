package com.moa.backend.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "프로젝트 심사 반려 요청")
public class RejectProjectRequest {
    @Schema(description = "반려 사유", example = "필수 서류 미비")
    private String reason;
}
