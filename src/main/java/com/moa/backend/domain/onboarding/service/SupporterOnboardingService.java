package com.moa.backend.domain.onboarding.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.backend.domain.onboarding.dto.SupporterOnboardingStatusResponse;
import com.moa.backend.domain.onboarding.dto.SupporterOnboardingStep1Request;
import com.moa.backend.domain.onboarding.dto.SupporterOnboardingStep2Request;
import com.moa.backend.domain.onboarding.model.OnboardingStatus;
import com.moa.backend.domain.user.entity.SupporterProfile;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.SupporterProfileRepository;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 한글 설명: 서포터 온보딩(관심사/선호도) 관련 비즈니스 로직
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SupporterOnboardingService {

    private final UserRepository userRepository;
    private final SupporterProfileRepository supporterProfileRepository;
    private final ObjectMapper objectMapper;

    // =====================================================================
    // 상태 조회
    // =====================================================================

    /**
     * 한글 설명: 현재 유저의 온보딩 상태 및 Step1/Step2 완료 여부 조회
     */
    @Transactional(readOnly = true)
    public SupporterOnboardingStatusResponse getStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        SupporterProfile profile = supporterProfileRepository.findByUserId(userId)
                .orElse(null);

        boolean step1Completed = false;
        boolean step2Completed = false;

        if (profile != null) {
            // Step1: interests(관심 카테고리)가 비어 있지 않으면 완료로 간주
            String interests = profile.getInterests();
            step1Completed = interests != null
                    && !interests.isBlank()
                    && !interests.equals("[]");

            // Step2: 예산/경험/유입경로/알림 중 하나라도 채워져 있으면 true 정도로만 판단
            step2Completed =
                    profile.getBudgetRange() != null ||
                            profile.getFundingExperience() != null ||
                            profile.getAcquisitionChannel() != null ||
                            profile.getNotificationPreference() != null;
        }

        return SupporterOnboardingStatusResponse.of(
                user.getOnboardingStatus(),
                step1Completed,
                step2Completed
        );
    }

    // =====================================================================
    // Step1 저장 (관심 카테고리 + 선호 스타일)
    // =====================================================================

    /**
     * 한글 설명:
     *  - 관심 카테고리(필수) + 선호 프로젝트 스타일(선택) 저장
     *  - 온보딩 상태는 여기서는 변경하지 않고, Step2 완료 시 COMPLETED 로 바꿈
     */
    @Transactional
    public void saveStep1(Long userId, SupporterOnboardingStep1Request request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        SupporterProfile profile = supporterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.SUPPORTER_PROFILE_NOT_FOUND));

        // 관심 카테고리 필수 체크 (백엔드 방어 로직)
        List<String> categories = request.interestCategories();
        if (categories == null || categories.isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "관심 카테고리는 1개 이상 선택해야 합니다.");
        }

        // ["TECH","DESIGN"] 같은 JSON 문자열로 저장
        String interestsJson = toJsonString(categories);
        // ["PRACTICAL","UNIQUE_GOODS"] 같은 JSON (enum 리스트도 그대로 직렬화)
        String preferredStylesJson = toJsonString(request.preferredStyles());

        profile.updateInterests(interestsJson);
        profile.updatePreferredStyles(preferredStylesJson);

        log.info("온보딩 Step1 저장 완료: userId={}, interests={}, preferredStyles={}",
                userId, interestsJson, preferredStylesJson);
    }

    // =====================================================================
    // Step2 저장 (추가 정보 + 알림 설정) + 온보딩 완료 처리
    // =====================================================================

    /**
     * 한글 설명:
     *  - 예산, 경험, 유입 경로, 알림 설정 등 선택 정보 저장
     *  - 호출 시 온보딩 상태를 COMPLETED 로 변경
     */
    @Transactional
    public void saveStep2(Long userId, SupporterOnboardingStep2Request request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        SupporterProfile profile = supporterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.SUPPORTER_PROFILE_NOT_FOUND));

        if (request.budgetRange() != null) {
            profile.updateBudgetRange(request.budgetRange());
        }
        if (request.fundingExperience() != null) {
            profile.updateFundingExperience(request.fundingExperience());
        }
        if (request.acquisitionChannel() != null) {
            profile.updateAcquisition(
                    request.acquisitionChannel(),
                    request.acquisitionChannelEtc()
            );
        }
        if (request.notificationPreference() != null) {
            profile.updateNotificationPreference(request.notificationPreference());
        }

        // 온보딩 최종 완료 처리
        user.updateOnboardingStatus(OnboardingStatus.COMPLETED);

        log.info("온보딩 Step2 저장 및 완료 처리: userId={}, status={}",
                userId, user.getOnboardingStatus());
    }

    // =====================================================================
    // 온보딩 스킵 (나중에 하기)
    // =====================================================================

    /**
     * 한글 설명: 온보딩 화면에서 "나중에 하기"를 눌렀을 때 호출
     *  - 이후에는 자동으로 온보딩을 띄우지 않도록 SKIPPED 로 상태 변경
     */
    @Transactional
    public void skip(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.updateOnboardingStatus(OnboardingStatus.SKIPPED);
        log.info("온보딩 스킵 처리: userId={}, status={}", userId, user.getOnboardingStatus());
    }

    // =====================================================================
    // 내부 유틸
    // =====================================================================

    /**
     * 한글 설명: 온보딩에서 받은 리스트(any type)를 JSON 문자열로 직렬화
     *  - List<String>, List<ProjectStylePreference> 등 아무 타입이나 허용
     */
    private String toJsonString(List<?> list) {
        if (list == null) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.error("리스트를 JSON 문자열로 변환하는 중 오류 발생", e);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "온보딩 데이터 직렬화 중 오류가 발생했습니다.");
        }
    }
}
