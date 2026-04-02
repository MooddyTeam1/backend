package com.moa.backend.domain.admin.controller;

import com.moa.backend.domain.admin.dto.RejectProjectRequest;
import com.moa.backend.domain.admin.service.AdminService;
import com.moa.backend.domain.project.dto.CreateProject.CreateProjectResponse;
import com.moa.backend.domain.project.dto.ProjectDetailResponse;
import com.moa.backend.domain.admin.dto.ProjectStatusResponse;
import com.moa.backend.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/project")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Project-Admin", description = "프로젝트 심사/승인/반려 (ADMIN)")
public class ProjectAdminController {

    private final AdminService adminService;

    //프로젝트 승인
    @PatchMapping("/{projectId}/approve")
    @Operation(summary = "프로젝트 승인", description = "심사 중(REVIEW) 상태 프로젝트를 승인합니다. 이미 승인/반려된 프로젝트는 승인할 수 없습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로젝트 승인 성공"),
            @ApiResponse(responseCode = "400", description = "승인할 수 없는 상태", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProjectStatusResponse> approveProject(
            @Parameter(description = "승인할 프로젝트 ID", example = "1200") @PathVariable Long projectId
    ) {
        ProjectStatusResponse response = adminService.approveProject(projectId);
        return ResponseEntity.ok(response);
    }

    //프로젝트 반려 (심사중, 승인됨, 공개예정 반려가능)
    @PatchMapping("/{projectId}/reject")
    @Operation(summary = "프로젝트 반려", description = "심사 중(REVIEW) 상태 프로젝트를 반려합니다. 반려 사유는 필수입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로젝트 반려 성공"),
            @ApiResponse(responseCode = "400", description = "반려 사유 누락 또는 상태 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProjectStatusResponse> rejectProject(
            @Parameter(description = "반려할 프로젝트 ID", example = "1200") @PathVariable Long projectId,
            @RequestBody RejectProjectRequest request
    ) {
        ProjectStatusResponse response = adminService.rejectProject(projectId, request.getReason());
        return ResponseEntity.ok(response);
    }

    //프로젝트 승인 대기 조회
    @GetMapping("/review")
    @Operation(summary = "승인 대기 프로젝트 목록 조회", description = "심사 대기 중인 프로젝트 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "목록 조회 성공")
    public ResponseEntity<List<CreateProjectResponse>> reviewProject() {
        return ResponseEntity.ok(adminService.getReviewProjects());
    }

    //프로젝트 승인대기 조회(검토페이지)
    @GetMapping("/review/{projectId}")
    @Operation(summary = "승인 대기 프로젝트 상세 조회", description = "승인 대기 프로젝트 상세를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProjectDetailResponse> projectDetailsReview(
            @Parameter(description = "조회할 프로젝트 ID", example = "1200") @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(adminService.getProjectDetailsReview(projectId));
    }
}
