package com.moa.backend.domain.admin.controller;

import com.moa.backend.domain.admin.dto.AdminProjectDetailResponse;
import com.moa.backend.domain.admin.dto.AdminProjectReviewResponse;
import com.moa.backend.domain.admin.dto.ProjectStatusResponse;
import com.moa.backend.domain.admin.dto.RejectProjectRequest;
import com.moa.backend.domain.admin.dto.RejectReasonPresetResponse;
import com.moa.backend.domain.admin.service.AdminProjectReviewService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "심사 대기 프로젝트 목록 조회")
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
    @Operation(summary = "프로젝트 심사 상세 조회")
    public ResponseEntity<AdminProjectDetailResponse> getProjectDetail(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long projectId
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
    @Operation(summary = "프로젝트 승인")
    public ResponseEntity<ProjectStatusResponse> approveProject(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long projectId
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
    @Operation(summary = "프로젝트 반려")
    public ResponseEntity<ProjectStatusResponse> rejectProject(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long projectId,
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
    @Operation(summary = "반려 사유 프리셋 조회")
    public ResponseEntity<RejectReasonPresetResponse> getRejectReasonPresets(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        RejectReasonPresetResponse response =
                adminProjectReviewService.getRejectReasonPresets(principal);
        return ResponseEntity.ok(response);
    }
}

