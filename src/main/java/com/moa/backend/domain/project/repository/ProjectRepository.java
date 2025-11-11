package com.moa.backend.domain.project.repository;

import com.moa.backend.domain.project.entity.*;

import java.time.LocalDate;
import java.util.Optional;

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

    List<Project> findByLifecycleStatusAndReviewStatus(ProjectLifecycleStatus status, ProjectReviewStatus reviewStatus);

    Optional<Project> findByIdAndLifecycleStatusAndReviewStatus(Long id, ProjectLifecycleStatus lifecycleStatus, ProjectReviewStatus reviewStatus);

    List<Project> findAllByMakerIdAndLifecycleStatusAndReviewStatus(Long id, ProjectLifecycleStatus lifecycleStatus, ProjectReviewStatus reviewStatus);

    Optional<Project> findByIdAndMaker_Id(Long projectId, Long makerId);

    long countByMakerIdAndLifecycleStatusAndReviewStatus(Long userId, ProjectLifecycleStatus lifecycleStatus, ProjectReviewStatus reviewStatus);

    List<Project> findByLifecycleStatusAndReviewStatusAndStartDateAfter(
            ProjectLifecycleStatus lifecycleStatus,
            ProjectReviewStatus reviewStatus,
            LocalDate date
    );

    List<Project> findByLifecycleStatusAndReviewStatusAndStartDate(
            ProjectLifecycleStatus lifecycleStatus,
            ProjectReviewStatus reviewStatus,
            LocalDate date
    );

    List<Project> findByLifecycleStatusAndReviewStatusAndEndDateBefore(
            ProjectLifecycleStatus lifecycleStatus,
            ProjectReviewStatus reviewStatus,
            LocalDate date
    );
}
