package com.moa.backend.domain.maker.controller;

import com.moa.backend.domain.maker.dto.MakerProfileResponse;
import com.moa.backend.domain.maker.dto.MakerProfileUpdateRequest;
import com.moa.backend.domain.maker.service.MakerProfileService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// 한글 설명: 내 메이커 프로필 조회/수정 API 컨트롤러
@RestController
@RequestMapping("/profile/me/maker")
@RequiredArgsConstructor
@Tag(name = "Maker-Profile", description = "내 메이커 프로필 조회/수정")
public class MakerProfileController {

    private final MakerProfileService makerProfileService;

    // 한글 설명: 내 메이커 프로필 조회
    @GetMapping
    @Operation(summary = "내 메이커 프로필 조회")
    public ResponseEntity<MakerProfileResponse> getProfile(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        MakerProfileResponse response = makerProfileService.getProfile(principal.getId());
        return ResponseEntity.ok(response);
    }

    // 한글 설명: 내 메이커 프로필 수정 (공통 + 사업자 정보 포함)
    @PatchMapping
    @Operation(summary = "내 메이커 프로필 수정")
    public ResponseEntity<MakerProfileResponse> updateProfile(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestBody MakerProfileUpdateRequest request
    ) {
        MakerProfileResponse response = makerProfileService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(response);
    }
}
