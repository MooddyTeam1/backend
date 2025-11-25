package com.moa.backend.domain.user.controller;

import com.moa.backend.domain.user.dto.SupporterProfileResponse;
import com.moa.backend.domain.user.dto.SupporterProfileUpdateRequest;
import com.moa.backend.domain.user.service.SupporterProfileService;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Supporter-Profile", description = "내 서포터 프로필 조회/수정")
public class SupporterProfileController {

    private final SupporterProfileService supporterProfileService;

    @GetMapping
    @Operation(summary = "내 서포터 프로필 조회")
    public ResponseEntity<SupporterProfileResponse> getProfile(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        SupporterProfileResponse response = supporterProfileService.getProfile(principal.getId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping
    @Operation(summary = "내 서포터 프로필 수정")
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
