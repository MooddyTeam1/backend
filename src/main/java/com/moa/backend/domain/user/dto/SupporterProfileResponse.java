package com.moa.backend.domain.user.dto;

import java.time.LocalDateTime;

public record SupporterProfileResponse(
        String displayName,
        String bio,
        String imageUrl,
        String phone,
        String address,
        String postalCode,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static SupporterProfileResponse of(
            String displayName,
            String bio,
            String imageUrl,
            String phone,
            String address,
            String postalCode,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new SupporterProfileResponse(
                displayName,
                bio,
                imageUrl,
                phone,
                address,
                postalCode,
                createdAt,
                updatedAt
        );
    }
}