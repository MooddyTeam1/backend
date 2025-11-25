package com.moa.backend.domain.maker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 한글 설명: 메이커 팔로우/언팔로우 응답 DTO.
 * - /public/makers/{makerId}/follow 에서 사용.
 */
@Schema(description = "메이커 팔로우/언팔로우 응답")
public record MakerFollowResponse(
        @Schema(description = "메이커 ID", example = "1000")
        String makerId,          // 한글 설명: 메이커 ID
        @Schema(description = "현재 팔로우 여부", example = "true")
        Boolean isFollowing,     // 한글 설명: true = 팔로우 중, false = 팔로우 아님
        @Schema(description = "현재 팔로워 수", example = "123")
        Integer followerCount    // 한글 설명: 현재 팔로워 수
) {
}
