package com.moa.backend.domain.project.service;

import com.moa.backend.domain.project.dto.*;
import com.moa.backend.domain.project.entity.*;

import java.util.List;

public interface ProjectService {

    //전체 조회
    List<ProjectDetailResponse> getAll();

    //단일 조회
    ProjectDetailResponse getById(Long projectId);

    //단일 조회 (로그인 유저 기준 찜 상태 포함)
    default ProjectDetailResponse getById(Long projectId, Long userId) {
        // 한글 설명: 기본 구현은 기존 메서드를 그대로 사용하는데,
        // 구현체에서 userId를 사용하는 버전으로 오버라이드해도 된다.
        return getById(projectId);
    }

    //제목으로 검색
    List<ProjectListResponse> searchByTitle(String keyword);

    //카테고리로 검색
    List<ProjectListResponse> getByCategory(Category category);

    //마감임박(7일전)
    List<ProjectListResponse> getClosingSoon();

    //프로젝트 상태별 요약
    StatusSummaryResponse getProjectSummary(Long userId);

    //특정 상태 프로젝트 필요한데이터만 조회
    List<?> getProjectByStatus(Long userId, ProjectLifecycleStatus lifecycle, ProjectReviewStatus review);

    //홈 화면 '지금 뜨는 프로젝트' 섹션용, 찜 많은 순 인기 프로젝트 조회.
    List<TrendingProjectResponse> getTrendingProjects(int size);

    //홈 화면 '방금 업로드된 신규 프로젝트' 섹션용 프로젝트 조회.
    // - 최근 N일 이내에 생성된 프로젝트 중에서, 승인된(APPROVED) + SCHEDULED/LIVE 상태만 반환한다.
    List<ProjectListResponse> getNewlyUploadedProjects(int size);

    // 한글 설명: 홈 화면 '성공 메이커의 새 프로젝트' 섹션용 조회.
    List<ProjectListResponse> getSuccessfulMakersNewProjects(int size);

    // 한글 설명: 홈 화면 '첫 도전 메이커 응원하기' 섹션용 조회.
    List<ProjectListResponse> getFirstChallengeMakerProjects(int size);

    // 한글 설명: 홈 화면 '목표 달성에 가까운 프로젝트' 섹션용 조회.
    // - 기준: LIVE + APPROVED 상태 프로젝트
    // - 정렬: (결제 완료 주문 금액 합계 / 목표 금액) 내림차순
    List<ProjectListResponse> getNearGoalProjects(int size);
}
