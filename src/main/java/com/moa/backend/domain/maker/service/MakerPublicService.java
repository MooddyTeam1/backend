package com.moa.backend.domain.maker.service;

import com.moa.backend.domain.follow.repository.SupporterFollowMakerRepository;
import com.moa.backend.domain.maker.dto.*;
import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.user.entity.SupporterProfile;
import com.moa.backend.domain.user.repository.SupporterProfileRepository;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 한글 설명: 메이커 공개 페이지(프로필/프로젝트/소식/상세 정보) 조회 전용 서비스.
 * - 인증이 없어도 접근 가능한 정보만 제공하되,
 *   로그인된 경우 isOwner / isFollowing 같은 개인화 정보도 함께 내려준다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MakerPublicService {

    private final MakerRepository makerRepository;
    private final SupporterProfileRepository supporterProfileRepository;
    private final SupporterFollowMakerRepository supporterFollowMakerRepository;
    // 프로젝트/소식/통계용 Repository는 추후 연동 가능 (현재는 빈 리스트/0 반환)

    /**
     * 한글 설명: 메이커 공개 프로필 조회.
     * - /public/makers/{makerId}
     */
    public MakerPublicProfileResponse getMakerPublicProfile(Long makerId) {
        Maker maker = makerRepository.findById(makerId)
                .orElseThrow(() -> new IllegalArgumentException("메이커를 찾을 수 없습니다. id=" + makerId));

        // 통계 값은 아직 연동 전이므로 0 / null 기본값 사용
        Long totalRaised = 0L;
        Integer totalSupporters = 0;
        Double satisfactionRate = null;

        // 로그인 사용자 기준 isOwner / isFollowing 계산
        Long currentUserId = getCurrentUserIdOrNull();
        boolean isOwner = false;
        Boolean isFollowing = null; // 비로그인 시 null, 로그인 + 서포터 프로필 없으면 false

        if (currentUserId != null) {
            isOwner = maker.getOwner() != null && maker.getOwner().getId().equals(currentUserId);

            // 서포터 프로필이 있는 경우에만 팔로우 여부를 검사한다.
            SupporterProfile supporter = supporterProfileRepository.findByUserId(currentUserId)
                    .orElse(null);

            if (supporter != null) {
                boolean following = supporterFollowMakerRepository.existsBySupporterAndMaker(supporter, maker);
                isFollowing = following;
            } else {
                isFollowing = false;
            }
        }

        // keywords 문자열 → DTO 목록 변환
        List<MakerKeywordDTO> keywordDTOs = parseKeywords(maker.getKeywords());

        return new MakerPublicProfileResponse(
                String.valueOf(maker.getId()),
                String.valueOf(maker.getOwner().getId()),
                maker.getName(),
                maker.getImageUrl(),
                maker.getProductIntro(),
                maker.getCoreCompetencies(),
                keywordDTOs.stream().map(MakerKeywordDTO::id).toList(),
                keywordDTOs,
                totalRaised,
                totalSupporters,
                satisfactionRate,
                isFollowing,
                isOwner
        );
    }

    /**
     * 한글 설명: 메이커 상세 정보 조회.
     * - /public/makers/{makerId}/info
     */
    public MakerDetailInfoResponse getMakerDetailInfo(Long makerId) {
        Maker maker = makerRepository.findById(makerId)
                .orElseThrow(() -> new IllegalArgumentException("메이커를 찾을 수 없습니다. id=" + makerId));

        String establishedAt = maker.getEstablishedAt() != null
                ? maker.getEstablishedAt().format(DateTimeFormatter.ISO_LOCAL_DATE)
                : null;

        return new MakerDetailInfoResponse(
                maker.getCoreCompetencies(),
                establishedAt,
                maker.getIndustryType(),
                maker.getBusinessItem(),
                maker.getBusinessNumber(),
                maker.getBusinessName(),
                maker.getOnlineSalesRegistrationNo(),
                maker.getRepresentative(),
                maker.getLocation(),
                maker.getContactEmail(),
                maker.getContactPhone(),
                maker.getMakerType() != null ? maker.getMakerType().name() : null
        );
    }

    /**
     * 한글 설명: 메이커 프로젝트 목록 조회.
     * - /public/makers/{makerId}/projects
     * - 현재는 실제 ProjectRepository 연동 전이므로 빈 목록/0값 반환.
     */
    public MakerProjectsPageResponse getMakerProjects(Long makerId, int page, int size, String status) {
        // 메이커 존재 여부 검증
        makerRepository.findById(makerId)
                .orElseThrow(() -> new IllegalArgumentException("메이커를 찾을 수 없습니다. id=" + makerId));

        List<ProjectSummaryDTO> content = List.of();

        return new MakerProjectsPageResponse(
                content,
                page,
                size,
                0L,
                0
        );
    }

    /**
     * 한글 설명: 메이커 소식 목록 조회.
     * - /public/makers/{makerId}/news
     * - 현재는 실제 MakerNewsRepository 연동 전이므로 빈 목록/0값 반환.
     */
    public MakerNewsPageResponse getMakerNews(Long makerId, int page, int size) {
        // 메이커 존재 여부 검증
        makerRepository.findById(makerId)
                .orElseThrow(() -> new IllegalArgumentException("메이커를 찾을 수 없습니다. id=" + makerId));

        List<MakerNewsDTO> content = List.of();

        return new MakerNewsPageResponse(
                content,
                page,
                size,
                0L,
                0
        );
    }

    // ================== 내부 유틸 메서드 ==================

    /**
     * 한글 설명: 현재 로그인한 사용자 ID를 반환한다.
     * - 비로그인 상태이면 null을 반환한다.
     */
    private Long getCurrentUserIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtUserPrincipal jwtUserPrincipal) {
            return jwtUserPrincipal.getId();
        }

        return null;
    }

    /**
     * 한글 설명: Maker 엔티티의 keywords 문자열(예: "친환경,소셜임팩트,B2B")
     * 을 파싱해서 MakerKeywordDTO 목록으로 변환하는 메서드.
     */
    private List<MakerKeywordDTO> parseKeywords(String keywordsString) {
        if (keywordsString == null || keywordsString.isBlank()) {
            return List.of();
        }

        List<String> names = Arrays.stream(keywordsString.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        List<MakerKeywordDTO> result = new ArrayList<>();
        for (String name : names) {
            result.add(new MakerKeywordDTO(null, name));
        }
        return result;
    }
}
