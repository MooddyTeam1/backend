package com.moa.backend.domain.maker.dto;

/**
 * 한글 설명: 메이커 팔로우 상태 조회 응답 DTO.
 * - /public/makers/{makerId}/follow/status 에서 사용.
 */
public record MakerFollowStatusResponse(
        String makerId,      // 한글 설명: 메이커 ID
        Boolean isFollowing  // 한글 설명: 현재 로그인 유저가 팔로우 중인지 여부
) {
}
