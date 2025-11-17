package com.moa.backend.domain.maker.service;

import com.moa.backend.domain.maker.dto.MakerProfileResponse;
import com.moa.backend.domain.maker.dto.MakerProfileUpdateRequest;
import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MakerProfileService {

    private final MakerRepository makerRepository;

    @Transactional(readOnly = true)
    public MakerProfileResponse getProfile(Long userId) {
        Maker maker = makerRepository.findByOwner_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("메이커 프로필을 찾을 수 없습니다."));
        return toResponse(maker);
    }

    @Transactional
    public MakerProfileResponse updateProfile(Long userId, MakerProfileUpdateRequest request) {
        Maker maker = makerRepository.findByOwner_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("메이커 프로필을 찾을 수 없습니다."));

        if (request.name() != null) {
            maker.updateName(request.name());
        }
        if (request.businessNumber() != null) {
            maker.updateBusinessNumber(request.businessNumber());
        }
        if (request.businessName() != null) {
            maker.updateBusinessName(request.businessName());
        }
        if (request.establishedAt() != null) {
            maker.updateEstablishedAt(request.establishedAt());
        }
        if (request.industryType() != null) {
            maker.updateIndustryType(request.industryType());
        }
        if (request.representative() != null) {
            maker.updateRepresentative(request.representative());
        }
        if (request.location() != null) {
            maker.updateLocation(request.location());
        }
        if (request.productIntro() != null) {
            maker.updateProductIntro(request.productIntro());
        }
        if (request.coreCompetencies() != null) {
            maker.updateCoreCompetencies(request.coreCompetencies());
        }
        if (request.imageUrl() != null) {
            maker.updateImageUrl(request.imageUrl());
        }
        if (request.contactEmail() != null) {
            maker.updateContactEmail(request.contactEmail());
        }
        if (request.contactPhone() != null) {
            maker.updateContactPhone(request.contactPhone());
        }
        if (request.techStackJson() != null) {
            maker.updateTechStackJson(request.techStackJson());
        }

        return toResponse(maker);
    }

    private MakerProfileResponse toResponse(Maker maker) {
        return MakerProfileResponse.of(
                maker.getId(),
                maker.getName(),
                maker.getBusinessNumber(),
                maker.getBusinessName(),
                maker.getEstablishedAt(),
                maker.getIndustryType(),
                maker.getRepresentative(),
                maker.getLocation(),
                maker.getProductIntro(),
                maker.getCoreCompetencies(),
                maker.getImageUrl(),
                maker.getContactEmail(),
                maker.getContactPhone(),
                maker.getTechStackJson(),
                maker.getCreatedAt(),
                maker.getUpdatedAt()
        );
    }
}