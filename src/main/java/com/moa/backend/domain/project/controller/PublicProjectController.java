package com.moa.backend.domain.project.controller;

import com.moa.backend.domain.project.dto.ProjectListResponse;
import com.moa.backend.domain.project.dto.TrendingProjectResponse;
import com.moa.backend.domain.project.service.ProjectService;
import com.moa.backend.domain.tracking.service.ProjectTrafficQueryService;
import com.moa.backend.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

// 한글 설명: 홈/공개 화면에서 사용하는 프로젝트 조회 전용 컨트롤러.
@RestController
@RequiredArgsConstructor
@RequestMapping("/public/projects")
@Tag(name = "Project-Public", description = "공개 프로젝트 조회(홈/트렌딩/카테고리 등)")
public class PublicProjectController {

    private final ProjectService projectService;
    private final ProjectTrafficQueryService projectTrafficQueryService;

    // 한글 설명: 홈 화면 상단 '지금 뜨는 프로젝트' 섹션 데이터를 조회하는 API.
    // - 기본 size=10, 쿼리 파라미터로 조절 가능 (예: /public/projects/trending?size=12)
    @GetMapping("/trending")
    @Operation(summary = "트렌딩 프로젝트 조회", description = "공개 가능한 프로젝트 중 트렌딩 목록을 조회합니다. size는 기본 5이며, 과도한 조회 크기는 제한될 수 있습니다.")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "트렌딩 조회 성공"),
            @ApiResponse(responseCode = "400", description = "요청 파라미터가 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<TrendingProjectResponse>> getTrendingProjects(
            @Parameter(description = "조회 개수(기본 5, 권장 최대 20)", example = "5") @RequestParam(name = "size", defaultValue = "5") int size
    ) {
        List<TrendingProjectResponse> result = projectService.getTrendingProjects(size);
        return ResponseEntity.ok(result);
    }

    // ===================== 마감 임박 프로젝트 =====================

    // 마감까지 7일 이내로 남은 진행 중(LIVE) + 승인된(APPROVED) 프로젝트 목록을 반환한다.
    // - 홈 화면 '곧 마감되는 프로젝트' 섹션에서 사용한다.
    @GetMapping("/closing-soon")
    @Operation(summary = "마감 임박 프로젝트 조회", description = "마감일이 임박한 공개 프로젝트 목록을 조회합니다.")
    @SecurityRequirements()
    @ApiResponse(responseCode = "200", description = "마감 임박 프로젝트 조회 성공")
    public ResponseEntity<List<ProjectListResponse>> getClosingSoonProjects() {
        List<ProjectListResponse> result = projectService.getClosingSoon();
        return ResponseEntity.ok(result);
    }

    // ===================== 방금 업로드된 신규 프로젝트 =====================

    // 최근 업로드된(생성된) 승인된 프로젝트 목록을 반환한다.
    // - 기준: 최근 3일 이내 createdAt
    // - 상태: SCHEDULED(공개 예정) + LIVE(진행 중)
    // - 응답이 빈 배열([])이면, 프론트에서
    //   "신규 프로젝트가 없습니다." 메시지를 노출하면 된다.
    @GetMapping("/newly-uploaded")
    @Operation(summary = "신규 업로드 프로젝트 조회", description = "최근 등록된 공개 프로젝트를 최신순으로 조회합니다.")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "신규 업로드 프로젝트 조회 성공"),
            @ApiResponse(responseCode = "400", description = "요청 파라미터가 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ProjectListResponse>> getNewlyUploadedProjects(
            @Parameter(description = "조회 개수(기본 10, 권장 최대 50)", example = "10") @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        List<ProjectListResponse> result = projectService.getNewlyUploadedProjects(size);
        return ResponseEntity.ok(result);
    }

    // ===================== 성공 메이커의 새 프로젝트 =====================

    // 한글 설명: 과거에 성공 이력이 있는 메이커들의
    // 현재 공개 예정/진행 중 프로젝트 목록을 반환한다.
    // - 프론트 문구: "성공 메이커의 새 프로젝트"
    @GetMapping("/success-maker-new")
    @Operation(summary = "성공 메이커의 새 프로젝트 조회", description = "과거 성공 이력이 있는 메이커의 신규 공개 프로젝트를 조회합니다.")
    @SecurityRequirements()
    @ApiResponse(responseCode = "200", description = "성공 메이커 프로젝트 조회 성공")
    public ResponseEntity<List<ProjectListResponse>> getSuccessfulMakersNewProjects(
            @Parameter(description = "조회 개수(기본 10, 권장 최대 50)", example = "10") @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        List<ProjectListResponse> result = projectService.getSuccessfulMakersNewProjects(size);
        return ResponseEntity.ok(result);
    }

    // ===================== 첫 도전 메이커 응원하기 =====================

    // 한글 설명: 해당 메이커에게 '첫 프로젝트'인 경우만 모아서 반환한다.
    // - 현재 LIVE 또는 SCHEDULED 상태 + APPROVED 조건을 만족하는 프로젝트만 대상.
    // - 프론트 문구: "첫 도전 메이커 응원하기"
    @GetMapping("/first-challenge")
    @Operation(summary = "첫 도전 메이커 프로젝트 조회", description = "첫 프로젝트에 도전하는 메이커의 공개 예정/진행 중 프로젝트를 조회합니다.")
    @SecurityRequirements()
    @ApiResponse(responseCode = "200", description = "첫 도전 메이커 프로젝트 조회 성공")
    public ResponseEntity<List<ProjectListResponse>> getFirstChallengeMakerProjects(
            @Parameter(description = "조회 개수(기본 10, 권장 최대 50)", example = "10") @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        List<ProjectListResponse> result = projectService.getFirstChallengeMakerProjects(size);
        return ResponseEntity.ok(result);
    }

    // ===================== 목표 달성에 가까운 프로젝트 =====================

    // 한글 설명:
    // - LIVE + APPROVED 상태 프로젝트 중,
    //   '결제 완료(PAID)' 주문 금액 기준으로 목표 달성률이 높은 순으로 정렬하여 반환한다.
    // - 프론트 문구:
    //   🧾 "목표 달성에 가까운 프로젝트"
    //   🔢 "달성률순 전체 보기"
    @GetMapping("/near-goal")
    @Operation(summary = "목표 달성률 높은 프로젝트 조회", description = "LIVE + APPROVED 프로젝트를 결제 완료 금액 기준 달성률 순으로 조회합니다.")
    @SecurityRequirements()
    @ApiResponse(responseCode = "200", description = "목표 달성률 기반 조회 성공")
    public ResponseEntity<List<ProjectListResponse>> getNearGoalProjects(
            @Parameter(description = "조회 개수(기본 10, 권장 최대 50)", example = "10") @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        List<ProjectListResponse> result = projectService.getNearGoalProjects(size);
        return ResponseEntity.ok(result);
    }

    // ===================== 예정된 펀딩 =====================

    @GetMapping("/scheduled")
    @Operation(summary = "공개 예정 프로젝트 조회", description = "공개 예정(SCHEDULED) 상태 프로젝트를 조회합니다.")
    @SecurityRequirements()
    @ApiResponse(responseCode = "200", description = "공개 예정 프로젝트 조회 성공")
    public ResponseEntity<List<ProjectListResponse>> getScheduledProjects(
            @Parameter(description = "조회 개수(기본 6, 권장 최대 30)", example = "6") @RequestParam(name = "size", defaultValue = "6") int size
    ) {
        // 한글 설명: 지정된 개수만큼 공개 예정 프로젝트 목록을 조회한다.
        List<ProjectListResponse> result = projectService.getScheduledProjects(size);
        return ResponseEntity.ok(result);
    }

    // ===================== 지금 많이 보고 있는 프로젝트 =====================

    /**
     * 📈 지금 많이 보고 있는 프로젝트
     * 예: /public/projects/most-viewed?minutes=60&size=6
     */
    @GetMapping("/most-viewed")
    @Operation(summary = "지금 많이 보고 있는 프로젝트 조회", description = "지정 시간 구간의 조회수 집계를 기반으로 많이 본 프로젝트를 조회합니다.")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "실시간 인기 조회 성공"),
            @ApiResponse(responseCode = "400", description = "minutes/size 파라미터가 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ProjectListResponse>> getMostViewedProjects(
            @Parameter(description = "조회 구간(분 단위, 기본 60)", example = "60") @RequestParam(name = "minutes", defaultValue = "60") long minutes,
            @Parameter(description = "조회 개수(기본 6, 권장 최대 30)", example = "6") @RequestParam(name = "size", defaultValue = "6") int size
    ) {
        Duration window = Duration.ofMinutes(minutes);

        String windowLabel;
        if (minutes < 60) {
            windowLabel = "최근 " + minutes + "분";
        } else {
            long hours = minutes / 60;
            windowLabel = "최근 " + hours + "시간";
        }

        // ✅ 한글 설명: 트래킹 서비스에서 이미 ProjectListResponse로 내려준다.
        List<ProjectListResponse> result =
                projectTrafficQueryService.getMostViewedProjects(window, size, windowLabel);

        return ResponseEntity.ok(result);
    }

    // ===================== 점수 기반 트렌딩 프로젝트 =====================

    /**
     * 🔥 지금 뜨는 프로젝트 (점수 기반 버전)
     * 예: /public/projects/trending-scored?size=10
     *
     * 기존 /trending (찜 순) 은 그대로 두고,
     * FE에서 이 API로 교체해도 되고, 둘 다 써도 됨.
     */
    @GetMapping("/trending-scored")
    @Operation(summary = "점수 기반 트렌딩 프로젝트 조회", description = "조회수/참여도 등 점수 기반 랭킹으로 트렌딩 프로젝트를 조회합니다.")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "점수 기반 트렌딩 조회 성공"),
            @ApiResponse(responseCode = "400", description = "size 파라미터가 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ProjectListResponse>> getTrendingScored(
            @Parameter(description = "조회 개수(기본 10, 권장 최대 50)", example = "10") @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        List<ProjectListResponse> result =
                projectTrafficQueryService.getTrendingProjectsWithScore(size);
        return ResponseEntity.ok(result);
    }

}
