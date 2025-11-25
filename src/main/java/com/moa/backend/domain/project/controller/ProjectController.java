package com.moa.backend.domain.project.controller;

import com.moa.backend.domain.project.dto.ProjectBookmarkResponse; // 🔥 북마크 응답 DTO
import com.moa.backend.domain.follow.service.SupporterProjectBookmarkService;
import com.moa.backend.domain.project.dto.*;
import com.moa.backend.domain.project.dto.CreateProject.CreateProjectRequest;
import com.moa.backend.domain.project.dto.CreateProject.CreateProjectResponse;
import com.moa.backend.domain.project.dto.TempProject.TempProjectRequest;
import com.moa.backend.domain.project.dto.TempProject.TempProjectResponse;
import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.project.service.ProjectCommandService;
import com.moa.backend.domain.project.service.ProjectService;
import com.moa.backend.domain.project.service.ProjectTempService;
import com.moa.backend.domain.tracking.service.ProjectTrafficQueryService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
@Tag(name = "Project", description = "프로젝트 생성/조회/임시저장/찜하기 API")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectCommandService projectCommandService;
    private final ProjectTempService projectTempService;

    // 한글 설명: 서포터 → 프로젝트 찜/해제 로직을 담당하는 서비스(follow 도메인).
    private final SupporterProjectBookmarkService supporterProjectBookmarkService;

    // ✅ 한글 설명: 프로젝트 트래픽/뷰 기록용 서비스
    private final ProjectTrafficQueryService projectTrafficQueryService;
    // ====================== 프로젝트 생성 / 조회 ======================

    //프로젝트 생성
    @PostMapping("/request")
    @Operation(summary = "프로젝트 생성 또는 임시본 제출", description = "새 프로젝트를 생성하거나 기존 임시 프로젝트를 심사 요청합니다. projectId가 없으면 신규 생성.")
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
    @Operation(summary = "프로젝트 전체 조회", description = "전체 프로젝트 상세 목록을 반환합니다.")
    public ResponseEntity<List<ProjectDetailResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAll());
    }

    //단일 조회 + 북마크 상태 포함
    @GetMapping("/id/{projectId}")
    @Operation(summary = "프로젝트 상세 조회", description = "프로젝트 ID로 상세 정보를 조회하고 북마크 상태를 포함해 반환합니다.")
    public ResponseEntity<ProjectDetailResponse> getProjectById(
            @Parameter(example = "1200") @PathVariable Long projectId,
            @AuthenticationPrincipal JwtUserPrincipal principal,
            jakarta.servlet.http.HttpServletRequest request // ✅ HttpServletRequest 주입
    ) {
        // ✅ 1) 로그인 유저 여부
        Long userId = (principal != null) ? principal.getId() : null;

        // ✅ 2) 세션 ID (지금은 간단히 userId 또는 IP 기반으로 구성)
        String sessionId = resolveSessionId(request, userId);

        // ✅ 3) 프로젝트 뷰 트래킹 기록
        //    - userId는 ProjectTrafficQueryService 쪽에서 필요하면 User 엔티티로 조회해서 사용
        projectTrafficQueryService.trackProjectView(
                projectId,
                userId,   // ← User 대신 Long userId 버전으로 바꾸는 게 편함(아래 설명)
                sessionId,
                request
        );

        // ✅ 4) 기본 프로젝트 상세 정보 조회
        ProjectDetailResponse response = projectService.getById(projectId);

        // ✅ 5) 북마크 상태 조회
        var bookmarkStatus = supporterProjectBookmarkService.getStatus(userId, projectId);
        response.setBookmarked(bookmarkStatus.bookmarked());
        response.setBookmarkCount(bookmarkStatus.bookmarkCount());

        return ResponseEntity.ok(response);
    }
    // 한글 설명: 유저/세션 기준으로 간단한 sessionId 생성 헬퍼
    private String resolveSessionId(jakarta.servlet.http.HttpServletRequest request, Long userId) {
        // 로그인 유저면 userId 기반, 비로그인 유저면 IP + UA 기반
        if (userId != null) {
            return "USER-" + userId;
        }
        String ip = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .map(v -> v.split(",")[0].trim())
                .orElse(request.getRemoteAddr());
        String ua = Optional.ofNullable(request.getHeader("User-Agent")).orElse("UNKNOWN");
        return ("ANON-" + ip + "-" + ua).substring(0, Math.min(100, ("ANON-" + ip + "-" + ua).length()));
    }

    //제목 검색
    @GetMapping("/search")
    @Operation(summary = "프로젝트 제목 검색", description = "키워드로 프로젝트를 검색합니다.")
    public ResponseEntity<List<ProjectListResponse>> searchProjects(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(projectService.searchByTitle(keyword));
    }

    //카테고리로 검색
    @GetMapping("/category")
    @Operation(summary = "카테고리별 프로젝트 조회", description = "카테고리 값을 기준으로 프로젝트 목록을 조회합니다.")
    public ResponseEntity<List<ProjectListResponse>> getProjectsByCategory(
            @RequestParam Category category
    ) {
        return ResponseEntity.ok(projectService.getByCategory(category));
    }

    //마감 임박(7일전)
    @Deprecated
    @GetMapping("/closing-soon")
    public ResponseEntity<List<ProjectListResponse>> getProjectsByClosingSoon() {
        return ResponseEntity.ok(projectService.getClosingSoon());
    }

    // ====================== 임시저장 프로젝트 ======================

    //프로젝트 임시저장
    @PostMapping("/temp")
    @Operation(summary = "프로젝트 임시저장", description = "작성 중인 프로젝트를 임시 저장합니다.")
    public ResponseEntity<TempProjectResponse> saveTempProject(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestBody TempProjectRequest request
    ) {
        return ResponseEntity.ok(projectTempService.saveTemp(principal.getId(), null, request));
    }

    //프로젝트 임시저장 수정
    @PatchMapping("/temp/{projectId}")
    @Operation(summary = "프로젝트 임시저장 수정", description = "임시 저장된 프로젝트를 수정합니다.")
    public ResponseEntity<TempProjectResponse> updateTempProject(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1200") @PathVariable Long projectId,
            @RequestBody TempProjectRequest request
    ) {
        return ResponseEntity.ok(projectTempService.saveTemp(principal.getId(), projectId, request));
    }

    //임시저장 프로젝트 삭제  🔥(develop 쪽 매핑 유지)
    @DeleteMapping("/temp/delete/{projectId}")
    @Operation(summary = "임시 프로젝트 삭제", description = "임시 저장된 프로젝트를 삭제합니다.")
    public ResponseEntity<Void> deleteTempProject(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1200") @PathVariable Long projectId
    ) {
        projectTempService.deleteTemp(principal.getId(), projectId);
        return ResponseEntity.noContent().build();
    }

    // ====================== 프로젝트 상태 관련 ======================

    //프로젝트 상태별 요약
    @GetMapping("/summary")
    @Operation(summary = "프로젝트 상태 요약", description = "메이커의 프로젝트 상태별 요약(예: 진행 중/심사 중)을 반환합니다.")
    public ResponseEntity<StatusSummaryResponse> getProjectSummary(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        return ResponseEntity.ok(projectService.getProjectSummary(principal.getId()));
    }

    //특정 상태 프로젝트 필요한데이터만 조회 (탭 눌러서)
    @GetMapping("/me/status")
    @Operation(summary = "상태별 프로젝트 조회", description = "메이커의 프로젝트를 라이프사이클/심사 상태로 필터링해 조회합니다.")
    public ResponseEntity<List<?>> getProjectByStatus(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestParam("lifecycle") ProjectLifecycleStatus lifecycleStatus,
            @RequestParam("review") ProjectReviewStatus reviewStatus
    ) {
        return ResponseEntity.ok(projectService.getProjectByStatus(principal.getId(), lifecycleStatus, reviewStatus));
    }

    //프로젝트 취소(심사중, 승인됨, 공개예정) 🔥(develop 쪽 매핑 유지)
    @PatchMapping("/cancel/{projectId}")
    @Operation(summary = "프로젝트 취소", description = "심사 중/승인됨/공개 예정 상태의 프로젝트를 취소합니다.")
    public ResponseEntity<ProjectListResponse> cancelProject(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1200") @PathVariable Long projectId
    ) {
        ProjectListResponse response = projectCommandService.canceledProject(principal.getId(), projectId);
        return ResponseEntity.ok(response);
    }

    // ====================== 프로젝트 찜하기 / 찜 해제 ======================

    // 한글 설명: 서포터 → 프로젝트 찜하기. (feature/follow 쪽 매핑 유지)
    @PostMapping("/{projectId}/bookmark")
    @Operation(summary = "프로젝트 찜하기", description = "서포터가 프로젝트를 찜 처리합니다.")
    public ResponseEntity<ProjectBookmarkResponse> bookmarkProject(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1200") @PathVariable Long projectId
    ) {
        Long userId = principal.getId();
        var status = supporterProjectBookmarkService.bookmark(userId, projectId);

        ProjectBookmarkResponse response = new ProjectBookmarkResponse(
                projectId,
                status.bookmarked(),
                status.bookmarkCount()
        );
        return ResponseEntity.ok(response);
    }

    // 한글 설명: 서포터 → 프로젝트 찜 해제. (feature/follow 쪽 매핑 유지)
    @DeleteMapping("/{projectId}/bookmark")
    @Operation(summary = "프로젝트 찜 해제", description = "서포터가 프로젝트 찜을 해제합니다.")
    public ResponseEntity<ProjectBookmarkResponse> unbookmarkProject(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1200") @PathVariable Long projectId
    ) {
        Long userId = principal.getId();
        var status = supporterProjectBookmarkService.unbookmark(userId, projectId);

        ProjectBookmarkResponse response = new ProjectBookmarkResponse(
                projectId,
                status.bookmarked(),
                status.bookmarkCount()
        );
        return ResponseEntity.ok(response);
    }
}
