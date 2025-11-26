// 한글 설명: 현재 로그인한 유저의 프로필 응답 DTO
package com.moa.backend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

import lombok.Builder;

@Schema(description = "로그인한 사용자의 프로필 정보")
@Builder
public record UserProfileResponse(
        // 한글 설명: User 테이블의 id (DB에선 bigint든 varchar든 문자열로 내려줌)
        @Schema(description = "사용자 ID", example = "1")
        String id,
        // 한글 설명: 이메일
        @Schema(description = "이메일", example = "user@example.com")
        String email,
        // 한글 설명: 이름
        @Schema(description = "이름", example = "홍길동")
        String name,
        // 한글 설명: 프로필 이미지 URL
        @Schema(description = "프로필 이미지 URL", example = "https://cdn.moa.com/profile/1.png")
        String imageUrl,
        // 한글 설명: 로그인 제공자 (credentials / google / kakao 등)
        @Schema(description = "로그인 제공자", example = "google")
        String provider,
        // 한글 설명: 권한 (USER / ADMIN 등)
        @Schema(description = "역할", example = "USER")
        String role,
        // 한글 설명: 가입 일시
        @Schema(description = "가입 일시", example = "2025-01-01T12:00:00")
        LocalDateTime createdAt
) {
}
