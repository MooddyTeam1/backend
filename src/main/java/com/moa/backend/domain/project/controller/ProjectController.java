package com.moa.backend.domain.project.controller;

import com.moa.backend.domain.project.dto.*;
import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.project.service.ProjectService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    //프로젝트 생성
    @PostMapping
    public ResponseEntity<CreateProjectResponse> createProject(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Valid @RequestBody CreateProjectRequest request
    ) {
        CreateProjectResponse response = projectService.createProject(principal.getId(), request);
        return ResponseEntity.ok(response);
    }

    //전체 조회
    @GetMapping("/all")
    public ResponseEntity<List<ProjectDetailResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAll());
    }

    //단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDetailResponse> getProjectById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(projectService.getById(id));
    }

    //제목 검색
    @GetMapping("/search")
    public ResponseEntity<List<ProjectDetailResponse>> searchProjects(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(projectService.searchByTitle(keyword));
    }

    //카테고리별 조회
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProjectDetailResponse>> getProjectsByCategory(
            @PathVariable Category category
    ) {
        return ResponseEntity.ok(projectService.getByCategory(category));
    }

    //프로젝트 임시저장
    @PostMapping("/temp")
    public ResponseEntity<TempProjectResponse> saveTempProject(
        @AuthenticationPrincipal JwtUserPrincipal principal,
        @RequestBody TempProjectRequest request
    ) {
        return ResponseEntity.ok(projectService.saveTemp(principal.getId(), request));
    }

    //프로젝트 임시저장 수정
    @PatchMapping("/temp/{projectId}")
    public ResponseEntity<TempProjectResponse> updateTempProject(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long projectId,
            @RequestBody TempProjectRequest request
    ) {
        return ResponseEntity.ok(projectService.updateTemp(principal.getId(), projectId, request));
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
