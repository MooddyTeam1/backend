package com.moa.backend.domain.admin.controller;

import com.moa.backend.domain.admin.dto.AdminMakerProfileResponse;
import com.moa.backend.domain.admin.service.AdminProjectReviewService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 한글 설명: 관리자 메이커 관리 전용 컨트롤러.
 * - 메이커 프로필 조회
 */
@RestController
@RequestMapping("/api/admin/maker")
@RequiredArgsConstructor
@Tag(name = "Admin-Maker", description = "관리자 메이커 관리")
public class AdminMakerController {

    private final AdminProjectReviewService adminProjectReviewService;

    /**
     * 한글 설명: 특정 메이커의 프로필 조회 API.
     * - 관리자 권한 필요
     * - 메이커 ID로 조회
     */
    @GetMapping("/{makerId}")
    @Operation(summary = "메이커 프로필 조회")
    public ResponseEntity<AdminMakerProfileResponse> getMakerProfile(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long makerId
    ) {
        AdminMakerProfileResponse response =
                adminProjectReviewService.getMakerProfile(principal, makerId);
        return ResponseEntity.ok(response);
    }
}

