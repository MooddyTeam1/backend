package com.moa.backend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "서포터 프로필 수정 요청")
public record SupporterProfileUpdateRequest(
        @Schema(description = "닉네임", example = "모아러버")
        String displayName,
        @Schema(description = "한 줄 소개", example = "착한 소비를 좋아해요")
        String bio,
        @Schema(description = "프로필 이미지 URL", example = "https://cdn.moa.com/profile/123.png")
        String imageUrl,
        @Schema(description = "전화번호", example = "010-1234-5678")
        String phone,
        @Schema(description = "주소1", example = "서울특별시 강남구 테헤란로 1")
        String address1,
        @Schema(description = "주소2", example = "101동 202호")
        String address2,
        @Schema(description = "우편번호", example = "06234")
        String postalCode,
        @Schema(description = "관심 카테고리 목록", example = "[\"TECH\",\"FOOD\"]")
        List<String> interests
) {
}
