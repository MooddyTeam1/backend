package com.moa.backend.domain.project.repository;

import com.moa.backend.domain.project.dto.TrendingProjectResponse;
import com.moa.backend.domain.project.entity.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    List<Project> findByLifecycleStatusAndReviewStatusAndEndDateBetween(
            ProjectLifecycleStatus lifecycleStatus,
            ProjectReviewStatus reviewStatus,
            LocalDate start,
            LocalDate end
    );

    List<Project> findByLifecycleStatusAndEndDate(
            ProjectLifecycleStatus lifecycleStatus,
            LocalDate date
    );

    /**
     * 펀딩 종료 스케줄러가 처리할 프로젝트 목록 조회.
     * (LIVE + APPROVED + 아직 결과 미정 + 종료일 <= 기준일)
     */
    List<Project> findByLifecycleStatusAndReviewStatusAndResultStatusAndEndDateBefore(
            ProjectLifecycleStatus lifecycleStatus,
            ProjectReviewStatus reviewStatus,
            ProjectResultStatus resultStatus,
            LocalDate endDate
    );

    // 한글 설명: 라이프사이클/심사 상태 조건에 맞는 프로젝트 중,
    // 서포터 찜(북마크) 개수가 많은 순으로 정렬하여 상위 N개를 조회한다.
    @Query("""
        SELECT new com.moa.backend.domain.project.dto.TrendingProjectResponse(
            p.id,
            p.title,
            p.summary,
            p.coverImageUrl,
            p.category,
            p.lifecycleStatus,
            COUNT(sb.id)
        )
        FROM Project p
        LEFT JOIN com.moa.backend.domain.follow.entity.SupporterBookmarkProject sb
               ON sb.project = p
        WHERE p.lifecycleStatus IN :statuses
          AND p.reviewStatus = :reviewStatus
        GROUP BY p.id, p.title, p.summary, p.coverImageUrl, p.category, p.lifecycleStatus
        HAVING COUNT(sb.id) > 0
        ORDER BY COUNT(sb.id) DESC, p.createdAt DESC
        """)
    List<TrendingProjectResponse> findTrendingProjects(
            @Param("statuses") List<ProjectLifecycleStatus> statuses,
            @Param("reviewStatus") ProjectReviewStatus reviewStatus,
            Pageable pageable
    );

    /**
     * 한글 설명: 최근 업로드된(생성일 기준) 신규 프로젝트 조회.
     * - 조건: 지정한 라이프사이클 상태 목록 + 특정 심사 상태 + createdAt >= 기준일
     * - 정렬: createdAt 내림차순(가장 최근 생성 프로젝트부터)
     */
    @Query("""
        SELECT p
        FROM Project p
        WHERE p.lifecycleStatus IN :statuses
          AND p.reviewStatus = :reviewStatus
          AND p.createdAt >= :createdAfter
        ORDER BY p.createdAt DESC
        """)
    List<Project> findNewlyUploadedProjects(
            @Param("statuses") List<ProjectLifecycleStatus> statuses,
            @Param("reviewStatus") ProjectReviewStatus reviewStatus,
            @Param("createdAfter") LocalDateTime createdAfter,
            Pageable pageable
    );
    /**
     * 한글 설명: 과거에 성공(SUCCESS)한 프로젝트가 하나 이상 있는 메이커의
     * 현재 진행/공개 예정(LIVE/SCHEDULED) 프로젝트를 조회한다.
     * - 조건: p.lifecycleStatus IN (:statuses)
     *        p.reviewStatus = :reviewStatus
     *        EXISTS (과거 성공 프로젝트)
     * - 정렬: 최신 생성일(createdAt) 내림차순
     */
    @Query("""
        SELECT p
        FROM Project p
        WHERE p.lifecycleStatus IN :statuses
          AND p.reviewStatus = :reviewStatus
          AND EXISTS (
              SELECT 1
              FROM Project past
              WHERE past.maker = p.maker
                AND past.resultStatus = :successResult
          )
        ORDER BY p.createdAt DESC
        """)
    List<Project> findNewProjectsBySuccessfulMakers(
            @Param("statuses") List<ProjectLifecycleStatus> statuses,
            @Param("reviewStatus") ProjectReviewStatus reviewStatus,
            @Param("successResult") ProjectResultStatus successResult,
            Pageable pageable
    );
    /**
     * 한글 설명: 해당 메이커에게 이 프로젝트가 '첫 프로젝트'인 경우만 조회한다.
     * - 조건: p.lifecycleStatus IN (:statuses)
     *        p.reviewStatus = :reviewStatus
     *        NOT EXISTS (같은 메이커의 다른 프로젝트)
     *   → 즉, 메이커 입장에서 아직 이 프로젝트 하나만 존재하는 상태.
     */
    @Query("""
        SELECT p
        FROM Project p
        WHERE p.lifecycleStatus IN :statuses
          AND p.reviewStatus = :reviewStatus
          AND NOT EXISTS (
              SELECT 1
              FROM Project other
              WHERE other.maker = p.maker
                AND other.id <> p.id
          )
        ORDER BY p.createdAt DESC
        """)
    List<Project> findFirstChallengeMakerProjects(
            @Param("statuses") List<ProjectLifecycleStatus> statuses,
            @Param("reviewStatus") ProjectReviewStatus reviewStatus,
            Pageable pageable
    );


    /**
     * 한글 설명:
     * - 최근에 생성된(업로드된) 프로젝트를 가져온다.
     * - 상태는 'LIVE' 또는 'SCHEDULED' 이면서, 심사 상태는 'APPROVED' 인 것만.
     * - createdAt 기준 최신순 정렬.
     */
    @Query("""
        select p
        from Project p
        where p.lifecycleStatus in :lifecycleStatuses
          and p.reviewStatus = :reviewStatus
        order by p.createdAt desc
        """)
    List<Project> findNewProjectsForHome(
            List<ProjectLifecycleStatus> lifecycleStatuses,
            ProjectReviewStatus reviewStatus,
            Pageable pageable
    );

    // ========== 통계 API용 메서드 ==========

    /**
     * 기간별 신규 프로젝트 수
     */
    Long countByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 기간별 심사 요청된 프로젝트 수 (REVIEW)
     */
    Long countByReviewStatusAndCreatedAtBetween(
            ProjectReviewStatus reviewStatus,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

    /**
     * 기간별 승인 완료된 프로젝트 수 (APPROVED)
     */
    Long countByReviewStatusAndApprovedAtBetween(
            ProjectReviewStatus reviewStatus,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

    /**
     * 기간 내 종료된 프로젝트 수 (ENDED, endDate 기준)
     */
    Long countByLifecycleStatusAndEndDateBetween(
            ProjectLifecycleStatus lifecycleStatus,
            LocalDate startDate,
            LocalDate endDate
    );
}
