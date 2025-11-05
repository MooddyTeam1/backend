package com.moa.backend.domain.project.controller;

import com.moa.backend.domain.project.dto.ProjectRequest;
import com.moa.backend.domain.project.dto.ProjectResponse;
import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.ProjectStatus;
import com.moa.backend.domain.project.service.ProjectService;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    //프로젝트 등록
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Valid @RequestBody ProjectRequest request
    ) {
        ProjectResponse response = projectService.createProject(principal.getId(), request);
        return ResponseEntity.ok(response);
    }

    //전체 조회
    @GetMapping("/all")
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAll());
    }

    //단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(projectService.getById(id));
    }

    //제목 검색
    @GetMapping("/search")
    public ResponseEntity<List<ProjectResponse>> searchProjects(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(projectService.searchByTitle(keyword));
    }

    //상태별 조회
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProjectResponse>> getProjectsByStatus(
            @PathVariable ProjectStatus status
    ) {
        return ResponseEntity.ok(projectService.getByStatus(status));
    }

    //카테고리별 조회
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProjectResponse>> getProjectsByCategory(
            @PathVariable Category category
    ) {
        return ResponseEntity.ok(projectService.getByCategory(category));
    }

    //프로젝트 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ProjectResponse> deleteProjectsById(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(projectService.deleteProject(principal.getId(), id));
    }
}
