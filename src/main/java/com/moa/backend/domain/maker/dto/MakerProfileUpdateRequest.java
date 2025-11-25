package com.moa.backend.domain.maker.dto;

import com.moa.backend.domain.maker.entity.MakerType;
import io.swagger.v3.oas.annotations.media.Schema;

// 한글 설명: /profile/me/maker PATCH 요청 DTO (common + business 묶음)
@Schema(description = "메이커 프로필 수정 요청")
public record MakerProfileUpdateRequest(
        @Schema(description = "메이커 유형", example = "INDIVIDUAL")
        MakerType makerType,                        // 한글 설명: 메이커 유형 (INDIVIDUAL / BUSINESS)
        @Schema(description = "공통 정보")
        MakerCommonUpdateRequest makerCommon,       // 한글 설명: 공통 정보
        @Schema(description = "사업자 정보(개인인 경우 null 가능)")
        MakerBusinessUpdateRequest makerBusiness    // 한글 설명: 사업자 정보 (개인인 경우 null 가능)
) {
}
