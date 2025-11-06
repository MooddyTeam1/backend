package com.moa.backend.domain.admin.service;

import com.moa.backend.domain.project.dto.ProjectResponse;
import com.moa.backend.domain.project.dto.ProjectStatusResponse;

import java.util.List;

public interface AdminService {


    //프로젝트 승인
    ProjectStatusResponse approveProject(Long projectId);

    //프로젝트 반려
    ProjectStatusResponse rejectProject(Long projectId, String reason);

    //프로젝트 승인 대기목록
    List<ProjectResponse> getDraftProjects();
}
