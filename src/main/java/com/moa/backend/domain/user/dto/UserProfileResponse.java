// 한글 설명: 현재 로그인한 유저의 프로필 응답 DTO
package com.moa.backend.domain.user.dto;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record UserProfileResponse(
        // 한글 설명: User 테이블의 id (DB에선 bigint든 varchar든 문자열로 내려줌)
        String id,
        // 한글 설명: 이메일
        String email,
        // 한글 설명: 이름
        String name,
        // 한글 설명: 프로필 이미지 URL
        String imageUrl,
        // 한글 설명: 로그인 제공자 (credentials / google / kakao 등)
        String provider,
        // 한글 설명: 권한 (USER / ADMIN 등)
        String role,
        // 한글 설명: 가입 일시
        LocalDateTime createdAt
) {
}
