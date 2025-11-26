package com.moa.backend.domain.maker.dto.publicpage;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 한글 설명: 메이커 팔로우 상태 조회 응답 DTO.
 * - /public/makers/{makerId}/follow/status 에서 사용.
 */
@Schema(description = "메이커 팔로우 상태 응답")
public record MakerFollowStatusResponse(
        @Schema(description = "메이커 ID", example = "1000")
        String makerId,      // 한글 설명: 메이커 ID
        @Schema(description = "현재 팔로우 여부", example = "true")
        Boolean isFollowing  // 한글 설명: 현재 로그인 유저가 팔로우 중인지 여부
) {
}
