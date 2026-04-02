package com.moa.backend.domain.admin.controller;

import com.moa.backend.domain.admin.dto.AdminProjectDetailResponse;
import com.moa.backend.domain.admin.dto.AdminProjectReviewResponse;
import com.moa.backend.domain.admin.dto.ProjectStatusResponse;
import com.moa.backend.domain.admin.dto.RejectProjectRequest;
import com.moa.backend.domain.admin.dto.RejectReasonPresetResponse;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 한글 설명: 관리자 프로젝트 심사 콘솔 전용 컨트롤러.
 * - 심사 대기 목록 조회
 * - 프로젝트 심사 상세 조회
 * - 승인 / 반려
 * - 반려 사유 프리셋 조회
 */
@RestController
@RequestMapping("/api/admin/project")
@RequiredArgsConstructor
@Tag(name = "Admin-Project-Review", description = "관리자 프로젝트 심사 관리")
public class AdminProjectReviewController {

    private final AdminProjectReviewService adminProjectReviewService;

    /**
     * 한글 설명: 심사 대기 프로젝트 목록 조회 API.
     * - reviewStatus == REVIEW 인 프로젝트만 조회
     */
    @GetMapping("/review")
    @Operation(summary = "심사 대기 프로젝트 목록 조회", description = "리뷰 상태가 REVIEW인 프로젝트만 조회합니다. 관리자 권한이 필요합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<AdminProjectReviewResponse>> getReviewProjects(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        List<AdminProjectReviewResponse> response =
                adminProjectReviewService.getReviewProjects(principal);
        return ResponseEntity.ok(response);
    }

    /**
     * 한글 설명: 특정 프로젝트의 심사 상세 조회 API.
     * - 프로젝트 기본 정보 + 메이커 프로필 + 리워드 목록 + 심사 상태/히스토리 포함
     */
    @GetMapping("/review/{projectId}")
    @Operation(summary = "프로젝트 심사 상세 조회", description = "프로젝트 심사 상세 정보를 조회합니다. 존재하지 않는 프로젝트는 조회할 수 없습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상세 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AdminProjectDetailResponse> getProjectDetail(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(description = "심사 상세 조회할 프로젝트 ID", example = "1200") @PathVariable Long projectId
    ) {
        AdminProjectDetailResponse response =
                adminProjectReviewService.getProjectDetail(principal, projectId);
        return ResponseEntity.ok(response);
    }

    /**
     * 한글 설명: 프로젝트 승인 API.
     * - reviewStatus 를 APPROVED 로 변경
     * - approvedAt 기록
     */
    @PatchMapping("/{projectId}/approve")
    @Operation(summary = "프로젝트 승인", description = "심사 중(REVIEW) 상태 프로젝트만 승인할 수 있습니다. 이미 승인/반려된 프로젝트는 승인 불가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로젝트 승인 성공"),
            @ApiResponse(responseCode = "400", description = "승인 불가능한 상태", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProjectStatusResponse> approveProject(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(description = "승인할 프로젝트 ID", example = "1200") @PathVariable Long projectId
    ) {
        ProjectStatusResponse response =
                adminProjectReviewService.approveProject(principal, projectId);
        return ResponseEntity.ok(response);
    }

    /**
     * 한글 설명: 프로젝트 반려 API.
     * - reviewStatus 를 REJECTED 로 변경
     * - rejectedAt, rejectedReason 기록
     */
    @PatchMapping("/{projectId}/reject")
    @Operation(summary = "프로젝트 반려", description = "심사 중(REVIEW) 상태 프로젝트를 반려합니다. 반려 사유는 필수이며 운영 이력으로 저장됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로젝트 반려 성공"),
            @ApiResponse(responseCode = "400", description = "반려 사유 누락 또는 상태 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProjectStatusResponse> rejectProject(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(description = "반려할 프로젝트 ID", example = "1200") @PathVariable Long projectId,
            @Valid @RequestBody RejectProjectRequest request
    ) {
        ProjectStatusResponse response =
                adminProjectReviewService.rejectProject(principal, projectId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 한글 설명: 반려 사유 프리셋 목록 조회 API.
     * - 운영팀이 자주 사용하는 반려 사유 텍스트 리스트 제공
     */
    @GetMapping("/reject-reason-presets")
    @Operation(summary = "반려 사유 프리셋 조회", description = "운영팀에서 자주 사용하는 반려 사유 프리셋 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프리셋 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RejectReasonPresetResponse> getRejectReasonPresets(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        RejectReasonPresetResponse response =
                adminProjectReviewService.getRejectReasonPresets(principal);
        return ResponseEntity.ok(response);
    }
}

