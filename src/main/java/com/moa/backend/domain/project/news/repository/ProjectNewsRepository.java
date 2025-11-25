package com.moa.backend.domain.project.news.repository;

import com.moa.backend.domain.project.news.entity.ProjectNews;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectNewsRepository extends JpaRepository<ProjectNews, Long> {
    List<ProjectNews> findByProject_IdOrderByPinnedDescCreatedAtDesc(Long projectId);
}
