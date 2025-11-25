package com.moa.backend.domain.project.community.repository;

import com.moa.backend.domain.project.community.entity.ProjectCommunity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectCommunityRepository extends JpaRepository<ProjectCommunity, Long> {
    List<ProjectCommunity> findByProject_IdOrderByCreatedAtDesc(Long projectId);
}
