package com.moa.backend.domain.news.repository;

import com.moa.backend.domain.news.entity.ProjectNews;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectNewsRepository extends JpaRepository<ProjectNews, Long> {
    List<ProjectNews> findByProject_IdOrderByPinnedDescCreatedAtDesc(Long projectId);
}
