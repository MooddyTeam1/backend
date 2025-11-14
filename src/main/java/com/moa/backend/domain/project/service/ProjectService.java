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
}
