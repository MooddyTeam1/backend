package com.moa.backend.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.backend.domain.follow.dto.SimpleMakerSummary;
import com.moa.backend.domain.follow.dto.SimpleSupporterSummary;
import com.moa.backend.domain.follow.entity.SupporterBookmarkProject;
import com.moa.backend.domain.follow.entity.SupporterFollowMaker;
import com.moa.backend.domain.follow.entity.SupporterFollowSupporter;
import com.moa.backend.domain.follow.repository.SupporterBookmarkProjectRepository;
import com.moa.backend.domain.follow.repository.SupporterFollowMakerRepository;
import com.moa.backend.domain.follow.repository.SupporterFollowSupporterRepository;
import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.project.dto.ProjectListResponse;
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

@Service
@RequiredArgsConstructor
public class SupporterProfileService {

    private final SupporterProfileRepository supporterProfileRepository;
    private final ObjectMapper objectMapper;

    // 팔로우 정보 조회용
    private final SupporterFollowSupporterRepository supporterFollowSupporterRepository;
    private final SupporterFollowMakerRepository supporterFollowMakerRepository;

    // ✅ 내가 찜한 프로젝트 조회용
    private final SupporterBookmarkProjectRepository supporterBookmarkProjectRepository;

    /**
     * 한글 설명: 내 서포터 프로필 조회
     * - 기본 프로필 정보
     * - 내가 팔로우한 서포터/메이커 정보
     * - 내가 찜한 프로젝트 목록
     */
    @Transactional(readOnly = true)
    public SupporterProfileResponse getProfile(Long userId) {
        SupporterProfile profile = getProfileOrThrow(userId);
        return toResponseWithFollows(profile);
    }

    /**
     * 한글 설명: 내 서포터 프로필 수정
     * - 변경 가능한 필드만 갱신
     * - 수정 후 최신 팔로우 / 찜 정보까지 포함해서 반환
     */
    @Transactional
    public SupporterProfileResponse updateProfile(Long userId, SupporterProfileUpdateRequest request) {
        SupporterProfile profile = getProfileOrThrow(userId);
        applyUpdates(profile, request);
        return toResponseWithFollows(profile);
    }

    // ================== 내부 헬퍼 메서드 ==================

    /**
     * 한글 설명: userId 기준으로 서포터 프로필 조회
     * - 없으면 AppException(NOT_FOUND) 발생
     */
    private SupporterProfile getProfileOrThrow(Long userId) {
        return supporterProfileRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.NOT_FOUND, "서포터 프로필을 찾을 수 없습니다."));
    }

    /**
     * 한글 설명: 요청값 기반으로 SupporterProfile 필드 업데이트
     * - null 이 아닌 값만 반영
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
            // 한글 설명: List<String> → JSON 문자열로 저장
            profile.updateInterests(toJson(request.interests()));
        }
    }

    /**
     * 한글 설명: 서포터 프로필 + 팔로우 카운트/리스트 + 찜한 프로젝트 리스트까지 포함한 DTO로 변환
     */
    private SupporterProfileResponse toResponseWithFollows(SupporterProfile profile) {

        // 1) 기본 프로필 부분은 기존 팩토리 메서드 재사용
        //    - 여기서 interestsJson → List<String> 으로 이미 변환됨
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
                .toList();

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
                .toList();

        // 5) ✅ 내가 찜한 프로젝트들 조회
        List<SupporterBookmarkProject> bookmarkRelations =
                supporterBookmarkProjectRepository.findBySupporter(profile);

        // 한글 설명: 각 북마크 관계에서 project 꺼내서 리스트용 DTO로 변환
        List<ProjectListResponse> bookmarkedProjects = bookmarkRelations.stream()
                .map(rel -> ProjectListResponse.base(rel.getProject()).build())
                .toList();

        // 6) 최종 응답 DTO 생성
        return new SupporterProfileResponse(
                base.userId(),
                base.displayName(),
                base.bio(),
                base.imageUrl(),
                base.phone(),
                base.address1(),
                base.address2(),
                base.postalCode(),
                base.interests(),              // 이미 List<String> 으로 파싱된 값
                base.createdAt(),
                base.updatedAt(),
                followingSupporterCount,
                followingMakerCount,
                followingSupporters,
                followingMakers,
                bookmarkedProjects             // ✅ 내가 찜한 프로젝트들
        );
    }

    /**
     * 한글 설명: List<String> → JSON 문자열
     */
    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("interests 직렬화에 실패했습니다.", e);
        }
    }
}
