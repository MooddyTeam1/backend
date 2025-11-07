package com.moa.backend.domain.project.service;

import com.moa.backend.domain.project.dto.CreateProjectRequest;
import com.moa.backend.domain.project.dto.CreateProjectResponse;
import com.moa.backend.domain.project.dto.ProjectDetailResponse;
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

}
