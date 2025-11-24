package com.moa.backend.domain.maker.dto;

/**
 * 한글 설명: 메이커 팔로우/언팔로우 응답 DTO.
 * - /public/makers/{makerId}/follow 에서 사용.
 */
public record MakerFollowResponse(
        String makerId,          // 한글 설명: 메이커 ID
        Boolean isFollowing,     // 한글 설명: true = 팔로우 중, false = 팔로우 아님
        Integer followerCount    // 한글 설명: 현재 팔로워 수
) {
}
