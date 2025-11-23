package com.moa.backend.domain.onboarding.controller;

import com.moa.backend.domain.onboarding.dto.SupporterOnboardingStatusResponse;
import com.moa.backend.domain.onboarding.dto.SupporterOnboardingStep1Request;
import com.moa.backend.domain.onboarding.dto.SupporterOnboardingStep2Request;
import com.moa.backend.domain.onboarding.service.SupporterOnboardingService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 한글 설명: 서포터 온보딩(관심사/선호도 설정) REST 컨트롤러
 *
 * 엔드포인트:
 *  - GET  /supporter/onboarding/status  : 온보딩 상태 조회
 *  - POST /supporter/onboarding/skip    : 온보딩 스킵 처리
 *  - POST /supporter/onboarding/step1   : Step1 저장 (관심 카테고리 + 선호 스타일)
 *  - POST /supporter/onboarding/step2   : Step2 저장 + 완료 처리
 */
@RestController
@RequestMapping("/api/supporter/onboarding") // 👈 여기 /api 추가
@RequiredArgsConstructor
public class SupporterOnboardingController {

    private final SupporterOnboardingService onboardingService;

    /**
     * 한글 설명: 온보딩 상태 및 Step1/2 완료 여부 조회
     */
    @GetMapping("/status")
    public ResponseEntity<SupporterOnboardingStatusResponse> getStatus(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        // 한글 설명: 현재 로그인 유저 ID 기준으로 서포터 온보딩 상태 조회
        SupporterOnboardingStatusResponse response =
                onboardingService.getStatus(principal.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * 한글 설명: 온보딩 Step1 저장 (관심 카테고리 + 선호 프로젝트 스타일)
     */
    @PostMapping("/step1")
    public ResponseEntity<Void> saveStep1(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestBody SupporterOnboardingStep1Request request
    ) {
        onboardingService.saveStep1(principal.getId(), request);
        return ResponseEntity.ok().build();
    }

    /**
     * 한글 설명: 온보딩 Step2 저장 (추가 정보 + 알림 설정) 및 온보딩 완료 처리
     */
    @PostMapping("/step2")
    public ResponseEntity<Void> saveStep2(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestBody SupporterOnboardingStep2Request request
    ) {
        onboardingService.saveStep2(principal.getId(), request);
        return ResponseEntity.ok().build();
    }

    /**
     * 한글 설명: 온보딩 스킵 ("나중에 하기")
     */
    @PostMapping("/skip")
    public ResponseEntity<Void> skip(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        onboardingService.skip(principal.getId());
        return ResponseEntity.ok().build();
    }
}