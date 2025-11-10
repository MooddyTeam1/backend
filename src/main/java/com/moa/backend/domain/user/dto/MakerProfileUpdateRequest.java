package com.moa.backend.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public record MakerProfileUpdateRequest(
        String name,
        String businessNumber,
        String businessName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate establishedAt,
        String industryType,
        String representative,
        String location,
        String productIntro,
        String coreCompetencies,
        String imageUrl,
        String contactEmail,
        String contactPhone,
        String techStackJson
) {
}