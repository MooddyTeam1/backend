package com.moa.backend.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

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
        if (request.address1() != null) {
            profile.updateAddress1(request.address1());
        }
        if (request.address2() != null) {
            profile.updateAddress2(request.address2());
        }
        if (request.postalCode() != null) {
            profile.updatePostalCode(request.postalCode());
        }
        if (request.interests() != null) {
            profile.updateInterests(toJson(request.interests())); // 🔥 List<String> → JSON 문자열
        }

        return toResponse(profile);
    }

    private SupporterProfileResponse toResponse(SupporterProfile profile) {
        return SupporterProfileResponse.of(
                profile.getUserId(),
                profile.getDisplayName(),
                profile.getBio(),
                profile.getImageUrl(),
                profile.getPhone(),
                profile.getAddress1(),
                profile.getAddress2(),
                profile.getPostalCode(),
                profile.getInterests(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("interests 직렬화에 실패했습니다.", e);
        }
    }
}
