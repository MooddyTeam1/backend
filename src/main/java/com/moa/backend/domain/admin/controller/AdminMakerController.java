package com.moa.backend.domain.admin.controller;

import com.moa.backend.domain.admin.dto.AdminMakerProfileResponse;
import com.moa.backend.domain.admin.service.AdminProjectReviewService;
import com.moa.backend.global.error.ErrorResponse;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "메이커 프로필 조회", description = "관리자가 특정 메이커의 프로필 상세를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메이커 프로필 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "메이커를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AdminMakerProfileResponse> getMakerProfile(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(description = "조회할 메이커 ID", example = "310") @PathVariable Long makerId
    ) {
        AdminMakerProfileResponse response =
                adminProjectReviewService.getMakerProfile(principal, makerId);
        return ResponseEntity.ok(response);
    }
}

