package com.moa.backend.domain.maker.service;

import com.moa.backend.domain.maker.dto.*;
import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 한글 설명: 메이커 프로필 조회/수정 비즈니스 로직을 담당하는 서비스
@Service
@RequiredArgsConstructor
public class MakerProfileService {

    private final MakerRepository makerRepository;

    // 한글 설명: 로그인 유저 ID 기준 메이커 프로필 조회
    @Transactional(readOnly = true)
    public MakerProfileResponse getProfile(Long userId) {
        Maker maker = makerRepository.findByOwner_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("메이커 프로필을 찾을 수 없습니다."));
        return toResponse(maker);
    }

    // 한글 설명: 로그인 유저 ID 기준 메이커 프로필 수정 (부분 업데이트)
    @Transactional
    public MakerProfileResponse updateProfile(Long userId, MakerProfileUpdateRequest request) {
        Maker maker = makerRepository.findByOwner_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("메이커 프로필을 찾을 수 없습니다."));

        // ===== 메이커 유형 업데이트 (정책에 따라 허용/불허 결정 가능) =====
        if (request.makerType() != null) {
            maker.updateMakerType(request.makerType());
        }

        // ===== 공통 영역 업데이트 =====
        MakerCommonUpdateRequest common = request.makerCommon();
        if (common != null) {
            if (common.name() != null) {
                maker.updateName(common.name());
            }
            if (common.establishedAt() != null) {
                maker.updateEstablishedAt(common.establishedAt());
            }
            if (common.industryType() != null) {
                maker.updateIndustryType(common.industryType());
            }
            if (common.representative() != null) {
                maker.updateRepresentative(common.representative());
            }
            if (common.location() != null) {
                maker.updateLocation(common.location());
            }
            if (common.productIntro() != null) {
                maker.updateProductIntro(common.productIntro());
            }
            if (common.coreCompetencies() != null) {
                maker.updateCoreCompetencies(common.coreCompetencies());
            }
            if (common.imageUrl() != null) {
                maker.updateImageUrl(common.imageUrl());
            }
            if (common.contactEmail() != null) {
                maker.updateContactEmail(common.contactEmail());
            }
            if (common.contactPhone() != null) {
                maker.updateContactPhone(common.contactPhone());
            }
            if (common.techStackJson() != null) {
                maker.updateTechStackJson(common.techStackJson());
            }
            if (common.keywords() != null) {
                maker.updateKeywords(common.keywords());
            }
        }

        // ===== 사업자 영역 업데이트 (BUSINESS일 때 의미가 있음) =====
        MakerBusinessUpdateRequest business = request.makerBusiness();
        if (business != null) {
            if (business.businessName() != null) {
                maker.updateBusinessName(business.businessName());
            }
            if (business.businessNumber() != null) {
                maker.updateBusinessNumber(business.businessNumber());
            }
            if (business.businessItem() != null) {
                maker.updateBusinessItem(business.businessItem());
            }
            if (business.onlineSalesRegistrationNo() != null) {
                maker.updateOnlineSalesRegistrationNo(business.onlineSalesRegistrationNo());
            }
        }

        return toResponse(maker);
    }

    // 한글 설명: 엔티티를 응답 DTO로 변환하는 유틸리티 메서드
    private MakerProfileResponse toResponse(Maker maker) {
        return MakerProfileResponse.of(
                maker.getId(),
                maker.getMakerType(),
                maker.getName(),
                maker.getBusinessNumber(),
                maker.getBusinessName(),
                maker.getEstablishedAt(),
                maker.getIndustryType(),
                maker.getBusinessItem(),
                maker.getOnlineSalesRegistrationNo(),
                maker.getRepresentative(),
                maker.getLocation(),
                maker.getProductIntro(),
                maker.getCoreCompetencies(),
                maker.getImageUrl(),
                maker.getContactEmail(),
                maker.getContactPhone(),
                maker.getTechStackJson(),
                maker.getKeywords(),
                maker.getCreatedAt(),
                maker.getUpdatedAt()
        );
    }
}
