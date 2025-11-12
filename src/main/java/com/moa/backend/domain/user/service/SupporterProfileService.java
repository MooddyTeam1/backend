package com.moa.backend.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.backend.domain.follow.entity.SupporterFollowMaker;
import com.moa.backend.domain.follow.entity.SupporterFollowSupporter;
import com.moa.backend.domain.follow.repository.SupporterFollowMakerRepository;
import com.moa.backend.domain.follow.repository.SupporterFollowSupporterRepository;
import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.follow.dto.SimpleMakerSummary;
import com.moa.backend.domain.follow.dto.SimpleSupporterSummary;
import com.moa.backend.domain.user.dto.SupporterProfileResponse;
import com.moa.backend.domain.user.dto.SupporterProfileUpdateRequest;
import com.moa.backend.domain.user.entity.SupporterProfile;
import com.moa.backend.domain.user.repository.SupporterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupporterProfileService {

    private final SupporterProfileRepository supporterProfileRepository;
    private final ObjectMapper objectMapper;

    // 팔로우 정보 조회용
    private final SupporterFollowSupporterRepository supporterFollowSupporterRepository;
    private final SupporterFollowMakerRepository supporterFollowMakerRepository;

    /**
     * 내 서포터 프로필 조회 (+ 내가 팔로우한 서포터/메이커 정보까지 포함)
     */
    @Transactional(readOnly = true)
    public SupporterProfileResponse getProfile(Long userId) {
        SupporterProfile profile = supporterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("서포터 프로필을 찾을 수 없습니다."));

        return toResponseWithFollows(profile);
    }

    /**
     * 내 서포터 프로필 수정 (+ 수정 후 최신 팔로우 정보까지 포함해서 반환)
     */
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
            profile.updateInterests(toJson(request.interests())); // List<String> → JSON 문자열
        }

        return toResponseWithFollows(profile);
    }

    /**
     * 서포터 프로필 + 팔로우 카운트/리스트까지 포함한 DTO로 변환
     */
    private SupporterProfileResponse toResponseWithFollows(SupporterProfile profile) {

        // 1) 내가 팔로우한 서포터/메이커 관계 조회
        List<SupporterFollowSupporter> supporterRelations =
                supporterFollowSupporterRepository.findByFollower(profile);

        List<SupporterFollowMaker> makerRelations =
                supporterFollowMakerRepository.findBySupporter(profile);

        long followingSupporterCount = supporterRelations.size();
        long followingMakerCount = makerRelations.size();

        // 2) 서포터 요약 DTO 리스트
        List<SimpleSupporterSummary> followingSupporters = supporterRelations.stream()
                .map(rel -> {
                    SupporterProfile target = rel.getFollowing();
                    return new SimpleSupporterSummary(
                            target.getUserId(),          // PK = user_id
                            target.getDisplayName(),
                            target.getImageUrl()
                    );
                })
                .collect(Collectors.toList());

        // 3) 메이커 요약 DTO 리스트
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

        // 4) interests(JSON 문자열 → List<String>) 파싱
        List<String> interests = parseInterests(profile.getInterests());

        // 5) 최종 응답 DTO 생성
        return new SupporterProfileResponse(
                profile.getUserId(),
                profile.getDisplayName(),
                profile.getBio(),
                profile.getImageUrl(),
                profile.getPhone(),
                profile.getAddress1(),
                profile.getAddress2(),
                profile.getPostalCode(),
                interests,
                profile.getCreatedAt(),
                profile.getUpdatedAt(),
                followingSupporterCount,
                followingMakerCount,
                followingSupporters,
                followingMakers
        );
    }

    /**
     * "['React','Spring']" 이런 JSON 문자열 → List<String>
     */
    private List<String> parseInterests(String raw) {
        if (raw == null || raw.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(raw, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
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
