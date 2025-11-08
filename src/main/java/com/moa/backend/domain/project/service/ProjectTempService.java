package com.moa.backend.domain.project.service;

import com.moa.backend.domain.project.dto.TempProjectRequest;
import com.moa.backend.domain.project.dto.TempProjectResponse;

public interface ProjectTempService {

    //프로젝트 임시저장
    TempProjectResponse saveTemp(Long userId, TempProjectRequest request);

    //프로젝트 임시저장 수정
    TempProjectResponse updateTemp(Long userId, Long projectId, TempProjectRequest request);
}
