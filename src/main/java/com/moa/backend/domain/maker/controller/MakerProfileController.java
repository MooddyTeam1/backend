package com.moa.backend.domain.maker.controller;

import com.moa.backend.domain.maker.dto.MakerProfileResponse;
import com.moa.backend.domain.maker.dto.MakerProfileUpdateRequest;
import com.moa.backend.domain.maker.service.MakerProfileService;
import com.moa.backend.global.error.ErrorResponse;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
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

// 한글 설명: 내 메이커 프로필 조회/수정 API 컨트롤러
@RestController
@RequestMapping("/profile/me/maker")
@RequiredArgsConstructor
@Tag(name = "Maker-Profile", description = "내 메이커 프로필 조회/수정")
public class MakerProfileController {

    private final MakerProfileService makerProfileService;

    // 한글 설명: 내 메이커 프로필 조회
    @GetMapping
    @Operation(summary = "내 메이커 프로필 조회", description = "로그인한 사용자의 메이커 프로필을 조회합니다. 메이커 프로필이 생성되지 않은 계정은 조회할 수 없습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "메이커 프로필을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MakerProfileResponse> getProfile(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        MakerProfileResponse response = makerProfileService.getProfile(principal.getId());
        return ResponseEntity.ok(response);
    }

    // 한글 설명: 내 메이커 프로필 수정 (공통 + 사업자 정보 포함)
    @PatchMapping
    @Operation(summary = "내 메이커 프로필 수정", description = "메이커 공통 정보와 사업자 정보를 수정합니다. 사업자 메이커는 사업자 필수값이 누락되면 저장할 수 없습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "메이커 프로필을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MakerProfileResponse> updateProfile(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestBody MakerProfileUpdateRequest request
    ) {
        MakerProfileResponse response = makerProfileService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(response);
    }
}
