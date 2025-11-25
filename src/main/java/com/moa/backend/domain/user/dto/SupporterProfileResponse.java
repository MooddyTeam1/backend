package com.moa.backend.domain.user.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.backend.domain.follow.dto.SimpleMakerSummary;
import com.moa.backend.domain.follow.dto.SimpleSupporterSummary;
import com.moa.backend.domain.project.dto.ProjectListResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 서포터 프로필 + 팔로우 정보 + 찜한 프로젝트 정보까지 포함하는 DTO
 */
@Schema(description = "서포터 프로필/팔로우/찜 목록 응답")
public record SupporterProfileResponse(
        @Schema(description = "사용자 ID", example = "1000")
        Long userId,
        @Schema(description = "닉네임", example = "햇살 서포터")
        String displayName,
        @Schema(description = "소개", example = "생활형 하드웨어 스타트업을 응원합니다.")
        String bio,
        @Schema(description = "프로필 이미지 URL", example = "https://cdn.moa.dev/avatars/user1.png")
        String imageUrl,
        @Schema(description = "전화번호", example = "010-2000-0001")
        String phone,
        @Schema(description = "주소1", example = "서울시 강남구 강남대로 321")
        String address1,
        @Schema(description = "주소2", example = "501호")
        String address2,
        @Schema(description = "우편번호", example = "06236")
        String postalCode,
        @Schema(description = "관심사 목록", example = "[\"하드웨어\",\"웰니스\"]")
        List<String> interests,
        @Schema(description = "생성 시각", example = "2024-11-10T09:15:00")
        LocalDateTime createdAt,
        @Schema(description = "수정 시각", example = "2024-11-12T10:30:00")
        LocalDateTime updatedAt,

        // ✅ 팔로우 관련 정보
        @Schema(description = "팔로잉 서포터 수", example = "3")
        long followingSupporterCount,
        @Schema(description = "팔로잉 메이커 수", example = "2")
        long followingMakerCount,
        @Schema(description = "팔로잉 서포터 목록")
        List<SimpleSupporterSummary> followingSupporters,
        @Schema(description = "팔로잉 메이커 목록")
        List<SimpleMakerSummary> followingMakers,

        // ✅ 내가 찜한 프로젝트들 (간단 리스트)
        @Schema(description = "찜한 프로젝트 목록")
        List<ProjectListResponse> bookmarkedProjects
) {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 🔹 예전처럼 "프로필만" 필요할 때 쓰는 팩토리
     *    팔로우 / 찜 정보는 0 / 빈 리스트로 채운다.
     */
    public static SupporterProfileResponse of(
            Long userId,
            String displayName,
            String bio,
            String imageUrl,
            String phone,
            String address1,
            String address2,
            String postalCode,
            String interestsJson,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new SupporterProfileResponse(
                userId,
                displayName,
                bio,
                imageUrl,
                phone,
                address1,
                address2,
                postalCode,
                parseInterests(interestsJson),
                createdAt,
                updatedAt,
                0L,
                0L,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()   // 🔸 bookmarkedProjects 기본값
        );
    }

    private static List<String> parseInterests(String raw) {
        if (raw == null || raw.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return OBJECT_MAPPER.readValue(raw, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            // 파싱 실패하면 일단 빈 배열로
            return Collections.emptyList();
        }
    }
}
