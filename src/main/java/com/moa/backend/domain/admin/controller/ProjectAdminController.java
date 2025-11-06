package com.moa.backend.domain.admin.controller;

import com.moa.backend.domain.admin.service.AdminService;
import com.moa.backend.domain.project.dto.ProjectResponse;
import com.moa.backend.domain.project.dto.ProjectStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/projects")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ProjectAdminController {

    private final AdminService adminService;

    //프로젝트 승인
    @PatchMapping("/{projectId}/approve")
    public ResponseEntity<ProjectStatusResponse> approveProject(
            @PathVariable Long projectId
    ) {
        ProjectStatusResponse response = adminService.approveProject(projectId);
        return ResponseEntity.ok(response);
    }

    //프로젝트 반려
    @PatchMapping("/{projectId}/reject")
    public ResponseEntity<ProjectStatusResponse> rejectProject(
            @PathVariable Long projectId,
            @RequestBody(required = false) String reason
    ) {
        ProjectStatusResponse response = adminService.rejectProject(projectId, reason);
        return ResponseEntity.ok(response);
    }

    //프로젝트 승인 대기 조회
    @GetMapping("/draft")
    public ResponseEntity<List<ProjectResponse>> draftProject() {
        return ResponseEntity.ok(adminService.getDraftProjects());
    }
}
