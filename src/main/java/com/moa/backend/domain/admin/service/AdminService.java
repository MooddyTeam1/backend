package com.moa.backend.domain.admin.service;

import com.moa.backend.domain.project.dto.CreateProjectResponse;
import com.moa.backend.domain.project.dto.ProjectDetailResponse;
import com.moa.backend.domain.admin.dto.ProjectStatusResponse;

import java.util.List;

public interface AdminService {


    //프로젝트 승인
    ProjectStatusResponse approveProject(Long projectId);

    //프로젝트 반려
    ProjectStatusResponse rejectProject(Long projectId, String reason);

    //프로젝트 승인 대기조회
    List<CreateProjectResponse> getReviewProjects();

    //프로젝트 승인대기 조회(검토페이지)
    ProjectDetailResponse getProjectDetailsReview(Long projectId);
}
