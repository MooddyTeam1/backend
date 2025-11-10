package com.moa.backend.domain.user.service;

import com.moa.backend.domain.user.dto.SupporterProfileResponse;
import com.moa.backend.domain.user.dto.SupporterProfileUpdateRequest;
import com.moa.backend.domain.user.entity.SupporterProfile;
import com.moa.backend.domain.user.repository.SupporterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupporterProfileService {

    private final SupporterProfileRepository supporterProfileRepository;

    @Transactional(readOnly = true)
    public SupporterProfileResponse getProfile(Long userId) {
        SupporterProfile profile = supporterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("서포터 프로필을 찾을 수 없습니다."));
        return toResponse(profile);
    }

    @Transactional
    public SupporterProfileResponse updateProfile(Long userId, SupporterProfileUpdateRequest request) {
        SupporterProfile profile = supporterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("서포터 프로필을 찾을 수 없습니다."));

        if (request.displayName() != null) {
            profile.updateDisplayName(request.displayName());
        }
        if (request.bio() != null) {
            profile.updateBio(request.bio());
        }
        if (request.imageUrl() != null) {
            profile.updateImageUrl(request.imageUrl());
        }
        if (request.phone() != null) {
            profile.updatePhone(request.phone());
        }
        if (request.address() != null) {
            profile.updateAddress(request.address());
        }
        if (request.postalCode() != null) {
            profile.updatePostalCode(request.postalCode());
        }

        return toResponse(profile);
    }

    private SupporterProfileResponse toResponse(SupporterProfile profile) {
        return SupporterProfileResponse.of(
                profile.getDisplayName(),
                profile.getBio(),
                profile.getImageUrl(),
                profile.getPhone(),
                profile.getAddress(),
                profile.getPostalCode(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}