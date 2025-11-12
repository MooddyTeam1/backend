package com.moa.backend.domain.user.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.backend.domain.follow.dto.SimpleMakerSummary;
import com.moa.backend.domain.follow.dto.SimpleSupporterSummary;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 서포터 프로필 + 팔로우 정보까지 포함하는 DTO
 */
public record SupporterProfileResponse(
        Long userId,
        String displayName,
        String bio,
        String imageUrl,
        String phone,
        String address1,
        String address2,
        String postalCode,
        List<String> interests,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        // ✅ 여기부터 추가된 팔로우 정보
        long followingSupporterCount,
        long followingMakerCount,
        List<SimpleSupporterSummary> followingSupporters,
        List<SimpleMakerSummary> followingMakers
) {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 🔹 예전처럼 "프로필만" 필요할 때 쓰는 팩토리
     *    팔로우 관련 필드는 0 / 빈 리스트로 채운다.
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
                Collections.emptyList()
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
