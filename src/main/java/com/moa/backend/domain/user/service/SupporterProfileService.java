package com.moa.backend.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.backend.domain.follow.dto.SimpleMakerSummary;
import com.moa.backend.domain.follow.dto.SimpleSupporterSummary;
import com.moa.backend.domain.follow.entity.SupporterFollowMaker;
import com.moa.backend.domain.follow.entity.SupporterFollowSupporter;
import com.moa.backend.domain.follow.repository.SupporterFollowMakerRepository;
import com.moa.backend.domain.follow.repository.SupporterFollowSupporterRepository;
import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.user.dto.SupporterProfileResponse;
import com.moa.backend.domain.user.dto.SupporterProfileUpdateRequest;
import com.moa.backend.domain.user.entity.SupporterProfile;
import com.moa.backend.domain.user.repository.SupporterProfileRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupporterProfileService {

    private final SupporterProfileRepository supporterProfileRepository;
    private final ObjectMapper objectMapper;

    private final SupporterFollowSupporterRepository supporterFollowSupporterRepository;
    private final SupporterFollowMakerRepository supporterFollowMakerRepository;

    /**
     * 내 서포터 프로필 조회
     * - 기본 프로필 정보
     * - 내가 팔로우한 서포터/메이커 카운트 + 리스트까지 함께 포함
     */
    @Transactional(readOnly = true)
    public SupporterProfileResponse getProfile(Long userId) {
        SupporterProfile profile = getProfileOrThrow(userId);
        return toResponseWithFollows(profile);
    }

    /**
     * 내 서포터 프로필 수정
     * - 수정 후 최신 팔로우 정보까지 포함해서 응답
     */
    @Transactional
    public SupporterProfileResponse updateProfile(Long userId, SupporterProfileUpdateRequest request) {
        SupporterProfile profile = getProfileOrThrow(userId);

        applyUpdates(profile, request);

        return toResponseWithFollows(profile);
    }

    // ================== 내부 헬퍼 메서드 ==================

    /**
     * userId 기준으로 서포터 프로필 조회
     * 없으면 AppException(NOT_FOUND) 발생
     */
    private SupporterProfile getProfileOrThrow(Long userId) {
        return supporterProfileRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.NOT_FOUND, "서포터 프로필을 찾을 수 없습니다."));
    }

    /**
     * 요청값 기반으로 SupporterProfile 필드 업데이트
     * (null이 아닌 값만 반영)
     */
    private void applyUpdates(SupporterProfile profile, SupporterProfileUpdateRequest request) {
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
            // List<String> → JSON 문자열로 저장
            profile.updateInterests(toJson(request.interests()));
        }
    }

    /**
     * 서포터 프로필 + 팔로우 정보까지 포함한 응답 DTO 생성
     *
     * 1. SupporterProfileResponse.of(...) 로 기본 프로필/관심사 매핑
     * 2. 팔로우 쿼리로 카운트/리스트 가져와서 다시 record 생성
     */
    private SupporterProfileResponse toResponseWithFollows(SupporterProfile profile) {

        // 1) 기본 프로필 부분은 기존 팩토리 메서드 재사용
        SupporterProfileResponse base = SupporterProfileResponse.of(
                profile.getUserId(),
                profile.getDisplayName(),
                profile.getBio(),
                profile.getImageUrl(),
                profile.getPhone(),
                profile.getAddress1(),
                profile.getAddress2(),
                profile.getPostalCode(),
                profile.getInterests(),      // JSON 문자열
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );

        // 2) 팔로우 관계 조회
        List<SupporterFollowSupporter> supporterRelations =
                supporterFollowSupporterRepository.findByFollower(profile);

        List<SupporterFollowMaker> makerRelations =
                supporterFollowMakerRepository.findBySupporter(profile);

        long followingSupporterCount = supporterRelations.size();
        long followingMakerCount = makerRelations.size();

        // 3) 서포터 요약 DTO 리스트 매핑
        List<SimpleSupporterSummary> followingSupporters = supporterRelations.stream()
                .map(rel -> {
                    SupporterProfile target = rel.getFollowing();
                    return new SimpleSupporterSummary(
                            target.getUserId(),          // supporter_profiles.user_id
                            target.getDisplayName(),
                            target.getImageUrl()
                    );
                })
                .collect(Collectors.toList());

        // 4) 메이커 요약 DTO 리스트 매핑
        List<SimpleMakerSummary> followingMakers = makerRelations.stream()
                .map(rel -> {
                    Maker maker = rel.getMaker();
                    return new SimpleMakerSummary(
                            maker.getId(),
                            maker.getName(),
                            maker.getImageUrl()
                    );
                })
                .collect(Collectors.toList());

        // 5) 기본 프로필 + 팔로우 정보까지 합쳐서 최종 DTO 생성
        return new SupporterProfileResponse(
                base.userId(),
                base.displayName(),
                base.bio(),
                base.imageUrl(),
                base.phone(),
                base.address1(),
                base.address2(),
                base.postalCode(),
                base.interests(),
                base.createdAt(),
                base.updatedAt(),
                followingSupporterCount,
                followingMakerCount,
                followingSupporters,
                followingMakers
        );
    }

    /**
     * List<String> → JSON 문자열
     */
    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("interests 직렬화에 실패했습니다.", e);
        }
    }
}
