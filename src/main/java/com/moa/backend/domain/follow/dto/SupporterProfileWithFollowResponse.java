// src/main/java/com/moa/backend/domain/user/dto/SupporterProfileWithFollowResponse.java
package com.moa.backend.domain.follow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "서포터 프로필 + 팔로우 정보 응답")
public class SupporterProfileWithFollowResponse {

    @Schema(description = "유저 ID", example = "1000")
    private Long userId;
    @Schema(description = "닉네임", example = "햇살 서포터")
    private String displayName;
    @Schema(description = "소개", example = "생활형 하드웨어 스타트업을 응원합니다.")
    private String bio;
    @Schema(description = "프로필 이미지 URL", example = "https://cdn.moa.dev/avatars/user1.png")
    private String imageUrl;
    @Schema(description = "전화번호", example = "010-2000-0001")
    private String phone;
    @Schema(description = "주소1", example = "서울시 강남구 강남대로 321")
    private String address1;
    @Schema(description = "주소2", example = "501호")
    private String address2;
    @Schema(description = "우편번호", example = "06236")
    private String postalCode;
    @Schema(description = "관심사 JSON 문자열", example = "[\"하드웨어\",\"웰니스\"]")
    private String interests;
    @Schema(description = "생성 시각", example = "2024-11-10T09:15:00")
    private LocalDateTime createdAt;
    @Schema(description = "수정 시각", example = "2024-11-12T10:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "팔로잉 서포터 수", example = "3")
    private long followingSupporterCount;
    @Schema(description = "팔로잉 메이커 수", example = "2")
    private long followingMakerCount;
    @Schema(description = "팔로우 중인 서포터 목록")
    private List<SimpleSupporterSummary> followingSupporters;
    @Schema(description = "팔로우 중인 메이커 목록")
    private List<SimpleMakerSummary> followingMakers;
}
