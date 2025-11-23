package com.moa.backend.domain.project.controller;

import com.moa.backend.domain.project.dto.ProjectListResponse;
import com.moa.backend.domain.project.dto.TrendingProjectResponse;
import com.moa.backend.domain.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 한글 설명: 홈/공개 화면에서 사용하는 프로젝트 조회 전용 컨트롤러.
@RestController
@RequiredArgsConstructor
@RequestMapping("/public/projects")
public class PublicProjectController {

    private final ProjectService projectService;

    // 한글 설명: 홈 화면 상단 '지금 뜨는 프로젝트' 섹션 데이터를 조회하는 API.
    // - 기본 size=10, 쿼리 파라미터로 조절 가능 (예: /public/projects/trending?size=12)
    @GetMapping("/trending")
    public ResponseEntity<List<TrendingProjectResponse>> getTrendingProjects(
            @RequestParam(name = "size", defaultValue = "5") int size
    ) {
        List<TrendingProjectResponse> result = projectService.getTrendingProjects(size);
        return ResponseEntity.ok(result);
    }

    // ===================== 마감 임박 프로젝트 =====================

    // 마감까지 7일 이내로 남은 진행 중(LIVE) + 승인된(APPROVED) 프로젝트 목록을 반환한다.
    // - 홈 화면 '곧 마감되는 프로젝트' 섹션에서 사용한다.
    @GetMapping("/closing-soon")
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
    public ResponseEntity<List<ProjectListResponse>> getNewlyUploadedProjects(
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        List<ProjectListResponse> result = projectService.getNewlyUploadedProjects(size);
        return ResponseEntity.ok(result);
    }
    // ===================== 성공 메이커의 새 프로젝트 =====================

    // 한글 설명: 과거에 성공 이력이 있는 메이커들의
    // 현재 공개 예정/진행 중 프로젝트 목록을 반환한다.
    // - 프론트 문구: "성공 메이커의 새 프로젝트"
    @GetMapping("/success-maker-new")
    public ResponseEntity<List<ProjectListResponse>> getSuccessfulMakersNewProjects(
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        List<ProjectListResponse> result = projectService.getSuccessfulMakersNewProjects(size);
        return ResponseEntity.ok(result);
    }

    // ===================== 첫 도전 메이커 응원하기 =====================

    // 한글 설명: 해당 메이커에게 '첫 프로젝트'인 경우만 모아서 반환한다.
    // - 현재 LIVE 또는 SCHEDULED 상태 + APPROVED 조건을 만족하는 프로젝트만 대상.
    // - 프론트 문구: "첫 도전 메이커 응원하기"
    @GetMapping("/first-challenge")
    public ResponseEntity<List<ProjectListResponse>> getFirstChallengeMakerProjects(
            @RequestParam(name = "size", defaultValue = "10") int size
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
    public ResponseEntity<List<ProjectListResponse>> getNearGoalProjects(
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        List<ProjectListResponse> result = projectService.getNearGoalProjects(size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/scheduled")
    public ResponseEntity<List<ProjectListResponse>> getScheduledProjects(
            @RequestParam(name = "size", defaultValue = "6") int size
    ) {
        // 한글 설명: 지정된 개수만큼 공개 예정 프로젝트 목록을 조회한다.
        List<ProjectListResponse> result = projectService.getScheduledProjects(size);
        return ResponseEntity.ok(result);
    }

}
