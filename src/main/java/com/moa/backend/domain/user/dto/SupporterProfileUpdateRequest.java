package com.moa.backend.domain.user.dto;

public record SupporterProfileUpdateRequest(
        String displayName,
        String bio,
        String imageUrl,
        String phone,
        String address,
        String postalCode
) {
}