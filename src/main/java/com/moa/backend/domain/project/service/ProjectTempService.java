package com.moa.backend.domain.project.service;

import com.moa.backend.domain.project.dto.CreateProject.CreateProjectRequest;
import com.moa.backend.domain.project.dto.CreateProject.CreateProjectResponse;
import com.moa.backend.domain.project.dto.TempProject.TempProjectRequest;
import com.moa.backend.domain.project.dto.TempProject.TempProjectResponse;

public interface ProjectTempService {

    //프로젝트 임시저장
    TempProjectResponse saveTemp(Long userId, Long projectId, TempProjectRequest request);

    //임시저장된 프로젝트 심사요청
    CreateProjectResponse requestTemp(Long userId, Long projectId, CreateProjectRequest request);

    //임시저장 프로젝트 삭제
    void deleteTemp(Long userId, Long projectId);
}
