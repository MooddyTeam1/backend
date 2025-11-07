package com.moa.backend.domain.project.service;

import com.moa.backend.domain.project.dto.*;
import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;

import java.util.List;

public interface ProjectService {

    //프로젝트 등록
    CreateProjectResponse createProject(Long userId, CreateProjectRequest request);

    //전체 조회
    List<ProjectDetailResponse> getAll();

    //단일 조회
    ProjectDetailResponse getById(Long id);

    //제목으로 검색
    List<ProjectDetailResponse> searchByTitle(String keyword);

    //상태별 조회
    List<ProjectDetailResponse> getByStatus(ProjectLifecycleStatus status);

    //카테고리별 조회
    List<ProjectDetailResponse> getByCategory(Category category);

    //프로젝트 임시저장
    TempProjectResponse saveTemp(Long userId, TempProjectRequest request);

    //프로젝트 임시저장 조회
    TempProjectResponse getTempProject(Long userId, Long projectId);

    //프로젝트 임시저장 수정
    TempProjectResponse updateTemp(Long userId, Long projectId, TempProjectRequest request);

}
