package com.moa.backend.domain.project.service;

import com.moa.backend.domain.project.dto.CreateProject.CreateProjectRequest;
import com.moa.backend.domain.project.dto.CreateProject.CreateProjectResponse;
import com.moa.backend.domain.project.dto.ProjectListResponse;

public interface ProjectCommandService {

    //프로젝트 생성
    CreateProjectResponse createProject(Long userId, CreateProjectRequest request);

    //프로젝트 취소 (심사중, 공개예정)
    ProjectListResponse canceledProject(Long userId, Long projectId);
}
