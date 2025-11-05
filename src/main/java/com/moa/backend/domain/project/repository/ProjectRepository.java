package com.moa.backend.domain.project.repository;

import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByTitle(String title);

    //제목으로 검색 (대소문자 구분없이)
    @Query("SELECT p FROM Project p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Project> searchByTitle(@Param("keyword") String keyword);

    //특정 카테고리별 프로젝트 조회
    List<Project> findByCategory(Category category);

    //특정 상태 프로젝트 조회
    List<Project> findByStatus(ProjectStatus status);
}

