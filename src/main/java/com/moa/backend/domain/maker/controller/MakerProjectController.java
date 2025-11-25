package com.moa.backend.api.maker.controller;

import com.moa.backend.domain.maker.dto.manageproject.MakerProjectListResponse;
import com.moa.backend.domain.maker.dto.manageproject.ProjectSummaryStatsResponse;
import com.moa.backend.domain.maker.service.MakerProjectService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 한글 설명:
 * - 메이커 마이페이지 > 내 프로젝트 목록/통계 전용 컨트롤러.
 * - URL:
 *   - GET /api/maker/projects
 *   - GET /api/maker/projects/stats/summary
 */
@RestController
@RequestMapping("/api/maker/projects")
@RequiredArgsConstructor
public class MakerProjectController {

    private final MakerProjectService makerProjectService;

    /**
     * 한글 설명:
     * - 메이커 프로젝트 목록 조회 API.
     * - Query 파라미터:
     *   - status: ALL/DRAFT/REVIEW/LIVE/ENDED_SUCCESS/ENDED_FAILED/REJECTED (기본값 ALL)
     *   - sortBy: recent/startDate/raised/deadline (기본값 recent)
     *   - page: 페이지 번호(1부터 시작, 기본값 1)
     *   - pageSize: 페이지 크기(기본값 12)
     */
    @GetMapping
    public ResponseEntity<MakerProjectListResponse> getMakerProjects(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @RequestParam(required = false, defaultValue = "recent") String sortBy,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "12") Integer pageSize
    ) {
        Long userId = principal.getId(); // 한글 설명: JwtUserPrincipal에서 로그인 유저 ID 추출

        MakerProjectListResponse response = makerProjectService.getMakerProjects(
                userId,
                status,
                sortBy,
                page,
                pageSize
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 한글 설명:
     * - 메이커 프로젝트 통계 요약 조회 API.
     * - 상단 카드 영역(전체 프로젝트 수, LIVE 수, 총 모금액, 이번 달 신규 프로젝트 수)에 사용.
     */
    @GetMapping("/stats/summary")
    public ResponseEntity<ProjectSummaryStatsResponse> getProjectSummaryStats(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        Long userId = principal.getId();

        ProjectSummaryStatsResponse stats = makerProjectService.getProjectSummaryStats(userId);
        return ResponseEntity.ok(stats);
    }
}
