package com.moa.backend.domain.user.controller;

import com.moa.backend.domain.user.dto.SupporterProfileResponse;
import com.moa.backend.domain.user.dto.SupporterProfileUpdateRequest;
import com.moa.backend.domain.user.service.SupporterProfileService;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile/me/suppoter")
@RequiredArgsConstructor
public class SupporterProfileController {

    private final SupporterProfileService supporterProfileService;

    @GetMapping
    public ResponseEntity<SupporterProfileResponse> getProfile(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        SupporterProfileResponse response = supporterProfileService.getProfile(principal.getId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<SupporterProfileResponse> updateProfile(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestBody SupporterProfileUpdateRequest request
    ) {
        if (principal == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        SupporterProfileResponse response =
                supporterProfileService.updateProfile(principal.getId(), request);

        return ResponseEntity.ok(response);
    }
}