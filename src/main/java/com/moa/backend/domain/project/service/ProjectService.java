package com.moa.backend.domain.project.service;

import com.moa.backend.domain.project.dto.*;
import com.moa.backend.domain.project.entity.*;

import java.util.List;

public interface ProjectService {

    //전체 조회
    List<ProjectDetailResponse> getAll();

    //단일 조회
    ProjectDetailResponse getById(Long projectId);

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
