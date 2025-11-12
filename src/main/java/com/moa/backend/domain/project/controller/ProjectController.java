package com.moa.backend.domain.project.controller;

import com.moa.backend.domain.project.dto.*;
import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.project.service.ProjectCommandService;
import com.moa.backend.domain.project.service.ProjectService;
import com.moa.backend.domain.project.service.ProjectTempService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectCommandService projectCommandService;
    private final ProjectTempService projectTempService;

    //프로젝트 생성
    @PostMapping("/request")
    public ResponseEntity<CreateProjectResponse> createProject(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Valid @RequestBody CreateProjectRequest request,
            @RequestParam(required = false) Long projectId
    ) {
        if (projectId == null) {
            return ResponseEntity.ok(projectCommandService.createProject(principal.getId(), request));
        } else {
            return ResponseEntity.ok(projectTempService.requestTemp(principal.getId(), projectId, request));
        }
    }

    //전체 조회
    @GetMapping("/all")
    public ResponseEntity<List<ProjectDetailResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAll());
    }

    //단일 조회
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponse> getProjectById(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(projectService.getById(projectId));
    }

    //제목 검색
    @GetMapping("/search")
    public ResponseEntity<List<ProjectListResponse>> searchProjects(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(projectService.searchByTitle(keyword));
    }

    //카테고리로 검색
    @GetMapping("/category")
    public ResponseEntity<List<ProjectListResponse>> getProjectsByCategory(
            @RequestParam Category category
    ) {
        return ResponseEntity.ok(projectService.getByCategory(category));
    }

    //마감 임박(7일전)
    @GetMapping("/closing-soon")
    public ResponseEntity<List<ProjectListResponse>> getProjectsByClosingSoon() {
        return ResponseEntity.ok(projectService.getClosingSoon());
    }

    //프로젝트 임시저장
    @PostMapping("/temp")
    public ResponseEntity<TempProjectResponse> saveTempProject(
        @AuthenticationPrincipal JwtUserPrincipal principal,
        @RequestBody TempProjectRequest request
    ) {
        return ResponseEntity.ok(projectTempService.saveTemp(principal.getId(),null, request));
    }

    //프로젝트 임시저장 수정
    @PatchMapping("/temp/{projectId}")
    public ResponseEntity<TempProjectResponse> updateTempProject(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long projectId,
            @RequestBody TempProjectRequest request
    ) {
        return ResponseEntity.ok(projectTempService.saveTemp(principal.getId(), projectId, request));
    }

    //프로젝트 상태별 요약
    @GetMapping("/summary")
    public ResponseEntity<StatusSummaryResponse> getProjectSummary(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        return ResponseEntity.ok(projectService.getProjectSummary(principal.getId()));
    }

    //특정 상태 프로젝트 필요한데이터만 조회 (탭눌러서)
    @GetMapping("/me/status")
    public ResponseEntity<List<?>> getProjectByStatus(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestParam("lifecycle") ProjectLifecycleStatus lifecycleStatus,
            @RequestParam("review") ProjectReviewStatus reviewStatus
    ) {
        return ResponseEntity.ok(projectService.getProjectByStatus(principal.getId(), lifecycleStatus, reviewStatus));
    }
}
