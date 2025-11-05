package com.moa.backend.domain.project.service;

import com.moa.backend.domain.project.dto.ProjectRequest;
import com.moa.backend.domain.project.dto.ProjectResponse;
import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.ProjectStatus;
import com.moa.backend.domain.user.entity.User;

import java.util.List;

public interface ProjectService {

    //프로젝트 등록
    ProjectResponse createProject(Long userId, ProjectRequest request);

    //전체 조회
    List<ProjectResponse> getAll();

    //단일 조회
    ProjectResponse getById(Long id);

    //제목으로 검색
    List<ProjectResponse> searchByTitle(String keyword);

    //상태별 조회
    List<ProjectResponse> getByStatus(ProjectStatus status);

    //카테고리별 조회
    List<ProjectResponse> getByCategory(Category category);

    //프로젝트 삭제
    ProjectResponse deleteProject(Long userid, Long projectId);
}
