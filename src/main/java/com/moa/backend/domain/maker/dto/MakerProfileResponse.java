package com.moa.backend.domain.maker.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MakerProfileResponse(
        Long id,
        String name,
        String businessNumber,
        String businessName,
        LocalDate establishedAt,
        String industryType,
        String representative,
        String location,
        String productIntro,
        String coreCompetencies,
        String imageUrl,
        String contactEmail,
        String contactPhone,
        String techStackJson,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MakerProfileResponse of(
            Long id,
            String name,
            String businessNumber,
            String businessName,
            LocalDate establishedAt,
            String industryType,
            String representative,
            String location,
            String productIntro,
            String coreCompetencies,
            String imageUrl,
            String contactEmail,
            String contactPhone,
            String techStackJson,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new MakerProfileResponse(
                id,
                name,
                businessNumber,
                businessName,
                establishedAt,
                industryType,
                representative,
                location,
                productIntro,
                coreCompetencies,
                imageUrl,
                contactEmail,
                contactPhone,
                techStackJson,
                createdAt,
                updatedAt
        );
    }
}