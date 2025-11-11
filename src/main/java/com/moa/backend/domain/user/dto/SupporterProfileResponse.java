package com.moa.backend.domain.user.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.backend.domain.user.entity.SupporterProfile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
        LocalDateTime updatedAt
) {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
                updatedAt
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
