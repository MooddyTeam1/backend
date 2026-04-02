// 파일: src/main/java/com/moa/backend/domain/maker/controller/MakerProjectController.java
package com.moa.backend.domain.maker.controller;

import com.moa.backend.domain.maker.dto.manageproject.MakerProjectDetailResponse;
import com.moa.backend.domain.maker.dto.manageproject.MakerProjectListResponse;
import com.moa.backend.domain.maker.dto.manageproject.ProjectSummaryStatsResponse;
import com.moa.backend.domain.maker.service.MakerProjectManageService;
import com.moa.backend.domain.maker.service.MakerProjectService;
import com.moa.backend.global.error.ErrorResponse;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 한글 설명:
 * - 메이커 마이페이지 > 내 프로젝트 목록/통계/상세 관리용 컨트롤러.
 * - URL:
 *   - GET  /api/maker/projects                      : 메이커의 프로젝트 목록
 *   - GET  /api/maker/projects/stats/summary        : 메이커 프로젝트 통계 요약
 *   - GET  /api/maker/projects/{projectId}          : 특정 프로젝트 상세 관리 데이터
 */
@RestController
@RequestMapping("/api/maker/projects")
@RequiredArgsConstructor
@Tag(name = "Maker-Project", description = "메이커 프로젝트 관리")
public class MakerProjectController {

    private final MakerProjectService makerProjectService;
    private final MakerProjectManageService makerProjectManageService;

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
    @Operation(summary = "메이커 프로젝트 목록 조회", description = "메이커의 프로젝트 목록을 상태/정렬/페이지 조건으로 조회합니다. page는 1부터 시작합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "요청 파라미터가 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MakerProjectListResponse> getMakerProjects(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(description = "상태 필터(ALL/DRAFT/REVIEW/LIVE/ENDED_SUCCESS/ENDED_FAILED/REJECTED)", example = "ALL") @RequestParam(required = false, defaultValue = "ALL") String status,
            @Parameter(description = "정렬 기준(recent/startDate/raised/deadline)", example = "recent") @RequestParam(required = false, defaultValue = "recent") String sortBy,
            @Parameter(description = "페이지 번호(1부터 시작)", example = "1") @RequestParam(required = false, defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기(기본 12)", example = "12") @RequestParam(required = false, defaultValue = "12") Integer pageSize
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
    @Operation(summary = "메이커 프로젝트 요약 통계 조회", description = "메이커 대시보드 상단 요약 통계(프로젝트 수, 모금액 등)를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요약 통계 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProjectSummaryStatsResponse> getProjectSummaryStats(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        Long userId = principal.getId();

        ProjectSummaryStatsResponse stats = makerProjectService.getProjectSummaryStats(userId);
        return ResponseEntity.ok(stats);
    }

    /**
     * 한글 설명:
     * - 메이커 프로젝트 상세 관리 화면 데이터 조회 API.
     * - 명세서의 `/api/maker/projects/{projectId}` 엔드포인트에 해당.
     */
    @GetMapping("/{projectId}")
    @Operation(summary = "메이커 프로젝트 상세 관리 조회", description = "메이커가 소유한 프로젝트의 관리 화면 상세 데이터를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상세 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "본인 프로젝트가 아니어서 접근 불가", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MakerProjectDetailResponse> getMakerProjectDetail(
            @Parameter(description = "조회할 프로젝트 ID", example = "1200") @PathVariable Long projectId,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        Long loginUserId = principal.getId();
        MakerProjectDetailResponse response = makerProjectManageService.getMakerProjectDetail(projectId, loginUserId);
        return ResponseEntity.ok(response);
    }
}
