package com.moa.backend.domain.project.service;

import com.moa.backend.domain.project.dto.CreateProjectRequest;
import com.moa.backend.domain.project.dto.CreateProjectResponse;

public interface ProjectCommandService {

    CreateProjectResponse createProject(Long userId, CreateProjectRequest request);
}
