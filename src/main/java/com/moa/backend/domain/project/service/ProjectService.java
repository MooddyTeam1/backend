package com.moa.backend.domain.project.service;

import com.moa.backend.domain.project.dto.*;
import com.moa.backend.domain.project.entity.*;

import java.util.List;

public interface ProjectService {

    //전체 조회
    List<ProjectDetailResponse> getAll();

    //단일 조회
    ProjectDetailResponse getById(Long id);

    //제목으로 검색
    List<ProjectDetailResponse> searchByTitle(String keyword);


    //카테고리별 조회
    List<ProjectDetailResponse> getByCategory(Category category);


    //프로젝트 상태별 요약
    StatusSummaryResponse getProjectSummary(Long userId);

    //특정 상태 프로젝트 필요한데이터만 조회
    List<?> getProjectByStatus(Long userId, ProjectLifecycleStatus lifecycle, ProjectReviewStatus review);
}
