package com.moa.backend.domain.maker.dto;

import com.moa.backend.domain.maker.entity.MakerType;

// 한글 설명: /profile/me/maker PATCH 요청 DTO (common + business 묶음)
public record MakerProfileUpdateRequest(
        MakerType makerType,                        // 한글 설명: 메이커 유형 (INDIVIDUAL / BUSINESS)
        MakerCommonUpdateRequest makerCommon,       // 한글 설명: 공통 정보
        MakerBusinessUpdateRequest makerBusiness    // 한글 설명: 사업자 정보 (개인인 경우 null 가능)
) {
}
