package com.moa.backend.domain.user.dto;

import java.util.List;

public record SupporterProfileUpdateRequest(
        String displayName,
        String bio,
        String imageUrl,
        String phone,
        String address1,
        String address2,
        String postalCode,
        List<String> interests
) {
}
