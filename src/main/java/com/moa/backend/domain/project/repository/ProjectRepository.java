package com.moa.backend.domain.project.repository;

import com.moa.backend.domain.project.dto.TrendingProjectResponse;
import com.moa.backend.domain.project.entity.*;
import com.moa.backend.domain.order.entity.OrderStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {

  boolean existsByTitle(String title);

  // 제목으로 검색 (대소문자 구분없이)
  @Query("SELECT p FROM Project p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  List<Project> searchByTitle(@Param("keyword") String keyword);

  // 특정 카테고리별 프로젝트 조회
  List<Project> findByCategory(Category category);

  List<Project> findByLifecycleStatusAndReviewStatus(ProjectLifecycleStatus status,
      ProjectReviewStatus reviewStatus);

  List<Project> findByReviewStatusOrderByRequestAtDesc(ProjectReviewStatus reviewStatus);

  /**
   * 한글 설명: 심사 상태별 프로젝트 목록을 조회하며 리워드와 메이커를 함께 로드 (fetch join).
   * - 관리자 심사 대기 목록 조회에서 사용.
   * - 리워드 이름 목록을 표시하기 위해 리워드 정보가 필요함.
   * - 목록 조회이므로 옵션 그룹은 로드하지 않음 (성능 고려).
   */
  @Query("""
      SELECT DISTINCT p
      FROM Project p
      LEFT JOIN FETCH p.rewards
      LEFT JOIN FETCH p.maker
      WHERE p.reviewStatus = :reviewStatus
      ORDER BY p.requestAt DESC
      """)
  List<Project> findByReviewStatusOrderByRequestAtDescWithRewards(
      @Param("reviewStatus") ProjectReviewStatus reviewStatus);

  /**
   * 한글 설명: 특정 심사 상태의 프로젝트 목록을 심사 요청 시각 기준 내림차순으로 조회.
   * - 심사 콘솔의 "심사 대기 목록"에서 사용.
   * - 리워드 정보를 포함하기 위해 fetch join 사용.
   */
  default List<Project> findByProjectReviewStatusOrderByRequestReviewAtDesc(
      ProjectReviewStatus projectReviewStatus) {
    return findByReviewStatusOrderByRequestAtDescWithRewards(projectReviewStatus);
  }

  /**
   * 한글 설명: 프로젝트 ID로 프로젝트를 조회하며 리워드와 메이커를 함께 로드 (fetch join).
   * - 관리자 심사 상세 조회에서 사용.
   * - 리워드와 메이커 정보가 필요한 경우 이 메서드를 사용.
   * - 리워드의 옵션 그룹과 옵션 값도 함께 로드.
   */
  @Query("""
      SELECT DISTINCT p
      FROM Project p
      LEFT JOIN FETCH p.rewards r
      LEFT JOIN FETCH r.optionGroups og
      LEFT JOIN FETCH og.optionValues
      LEFT JOIN FETCH p.maker m
      LEFT JOIN FETCH m.owner
      WHERE p.id = :projectId
      """)
  Optional<Project> findByIdWithRewardsAndMaker(@Param("projectId") Long projectId);

  Optional<Project> findByIdAndLifecycleStatusAndReviewStatus(Long id, ProjectLifecycleStatus lifecycleStatus,
      ProjectReviewStatus reviewStatus);

  List<Project> findAllByMakerIdAndLifecycleStatusAndReviewStatus(Long id, ProjectLifecycleStatus lifecycleStatus,
      ProjectReviewStatus reviewStatus);

  Optional<Project> findByIdAndMaker_Id(Long projectId, Long makerId);

  long countByMakerIdAndLifecycleStatusAndReviewStatus(Long userId, ProjectLifecycleStatus lifecycleStatus,
      ProjectReviewStatus reviewStatus);

  List<Project> findByLifecycleStatusAndReviewStatusAndStartDateAfter(
      ProjectLifecycleStatus lifecycleStatus,
      ProjectReviewStatus reviewStatus,
      LocalDate date);

  List<Project> findByLifecycleStatusAndReviewStatusAndStartDate(
      ProjectLifecycleStatus lifecycleStatus,
      ProjectReviewStatus reviewStatus,
      LocalDate date);

  List<Project> findByLifecycleStatusAndReviewStatusAndEndDateBefore(
      ProjectLifecycleStatus lifecycleStatus,
      ProjectReviewStatus reviewStatus,
      LocalDate date);

  List<Project> findByLifecycleStatusAndReviewStatusAndEndDateBetween(
      ProjectLifecycleStatus lifecycleStatus,
      ProjectReviewStatus reviewStatus,
      LocalDate start,
      LocalDate end);

  List<Project> findByLifecycleStatusAndEndDate(
      ProjectLifecycleStatus lifecycleStatus,
      LocalDate date);

  /**
   * 펀딩 종료 스케줄러가 처리할 프로젝트 목록 조회.
   * (LIVE + APPROVED + 아직 결과 미정 + 종료일 <= 기준일)
   */
  List<Project> findByLifecycleStatusAndReviewStatusAndResultStatusAndEndDateBefore(
      ProjectLifecycleStatus lifecycleStatus,
      ProjectReviewStatus reviewStatus,
      ProjectResultStatus resultStatus,
      LocalDate endDate);

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
      Pageable pageable);

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
      Pageable pageable);

  /**
   * 한글 설명: 과거에 성공(SUCCESS)한 프로젝트가 하나 이상 있는 메이커의
   * 현재 진행/공개 예정(LIVE/SCHEDULED) 프로젝트를 조회한다.
   * - 조건: p.lifecycleStatus IN (:statuses)
   * p.reviewStatus = :reviewStatus
   * EXISTS (과거 성공 프로젝트)
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
        FROM Project p
        LEFT JOIN com.moa.backend.domain.follow.entity.SupporterBookmarkProject sb
               ON sb.project = p
        WHERE p.lifecycleStatus IN :statuses
          AND p.reviewStatus = :reviewStatus
        GROUP BY p.id, p.title, p.summary, p.coverImageUrl, p.category, p.lifecycleStatus
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

    /**
     * 종료일 기준 결과 상태 카운트
     */
    Long countByResultStatusAndEndDateBetween(
            ProjectResultStatus resultStatus,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * 시작일 기준 카운트
     */
    Long countByStartDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * 시작일 기준 + 결과 상태 카운트
     */
    Long countByStartDateBetweenAndResultStatus(
            LocalDate startDate,
            LocalDate endDate,
            ProjectResultStatus resultStatus
    );

    /**
     * 종료일 기준 카운트
     */
    Long countByEndDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * 종료일 기준 + 결과 상태 카운트
     */
    Long countByEndDateBetweenAndResultStatus(
            LocalDate startDate,
            LocalDate endDate,
            ProjectResultStatus resultStatus
    );

    /**
     * 목표금액 구간 + 종료일 기준 카운트
     */
    Long countByGoalAmountBetweenAndEndDateBetween(
            Long minGoal,
            Long maxGoal,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * 목표금액 구간 + 종료일 + 결과 상태 카운트
     */
    Long countByGoalAmountBetweenAndEndDateBetweenAndResultStatus(
            Long minGoal,
            Long maxGoal,
            LocalDate startDate,
            LocalDate endDate,
            ProjectResultStatus resultStatus
    );

    /**
     * 카테고리별 종료일 기준 카운트
     */
    Long countByCategoryAndEndDateBetween(
            Category category,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * 카테고리별 종료일 + 결과 상태 카운트
     */
    Long countByCategoryAndEndDateBetweenAndResultStatus(
            Category category,
            LocalDate startDate,
            LocalDate endDate,
            ProjectResultStatus resultStatus
    );

    /**
     * 프로젝트 성과 리포트용 집계 (프로젝트별)
     * 결과: Object[] {projectId, projectName, makerId, makerName, category, goalAmount, endDate, resultStatus, fundingAmount, supporterCount, bookmarkCount}
     */
    @Query("""
        SELECT p.id,
               p.title,
               m.id,
               m.name,
               p.category,
               p.goalAmount,
               p.endDate,
               p.resultStatus,
               COALESCE(SUM(o.totalAmount), 0) as fundingAmount,
               COUNT(DISTINCT o.user.id) as supporterCount,
               COUNT(DISTINCT sb.id) as bookmarkCount
        FROM Project p
        JOIN p.maker m
        LEFT JOIN Order o ON o.project = p AND o.status = :status
        LEFT JOIN com.moa.backend.domain.follow.entity.SupporterBookmarkProject sb
               ON sb.project = p
        WHERE (:category IS NULL OR p.category = :category)
          AND (:makerId IS NULL OR m.id = :makerId)
        GROUP BY p.id, p.title, m.id, m.name, p.category, p.goalAmount, p.endDate, p.resultStatus
        ORDER BY COALESCE(SUM(o.totalAmount), 0) DESC
        """)
    List<Object[]> findProjectPerformanceStats(
            @Param("status") OrderStatus status,
            @Param("category") Category category,
            @Param("makerId") Long makerId
    );

    // 한글 설명: '예정되어 있는 펀딩' 섹션용 프로젝트 조회
// - 조건:
//   - lifecycleStatus = SCHEDULED
//   - reviewStatus = APPROVED
//   - liveStartAt >= now (아직 시작 전이거나 곧 시작할 프로젝트)
//   - (선택) liveEndAt > now 인 경우만 포함하고 싶으면 AND 조건 추가
    @Query("""
    SELECT p
    FROM Project p
    WHERE p.lifecycleStatus = :lifecycleStatus
      AND p.reviewStatus = :reviewStatus
      AND p.liveStartAt >= :now
    ORDER BY p.liveStartAt ASC, p.createdAt DESC
    """)
    List<Project> findScheduledProjects(
            @Param("lifecycleStatus") ProjectLifecycleStatus lifecycleStatus,
            @Param("reviewStatus") ProjectReviewStatus reviewStatus,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    /**
     * 한글 설명:
     * - 프로젝트 ID와 메이커 소유자(user.id)로 권한 체크용 존재 여부 확인.
     * - ownerUserId == users.id (Maker.owner.id)
     */
    boolean existsByIdAndMaker_Owner_Id(Long projectId, Long ownerUserId);

    /**
     * 한글 설명: 특정 메이커(maker.id)에 속한 모든 프로젝트 조회.
     * - 메이커 마이페이지(내 프로젝트 관리)에서 기본 데이터 소스로 사용한다.
     */
    List<Project> findAllByMakerId(Long makerId);

    /**
     * 한글 설명:
     * 메이커 홈(공개 화면)에서 사용할 프로젝트 목록 조회용 쿼리.
     *
     * 포함되는 프로젝트:
     *  - lifecycleStatus = SCHEDULED (공개 예정)
     *  - lifecycleStatus = LIVE (진행 중)
     *  - lifecycleStatus = ENDED 이면서 resultStatus = SUCCESS (성공 종료)
     *
     * 정렬은 Pageable의 Sort 설정을 그대로 사용한다.
     */
    @Query("""
        select p
        from Project p
        where p.maker.id = :makerId
          and (
              p.lifecycleStatus = com.moa.backend.domain.project.entity.ProjectLifecycleStatus.SCHEDULED
              or p.lifecycleStatus = com.moa.backend.domain.project.entity.ProjectLifecycleStatus.LIVE
              or (
                    p.lifecycleStatus = com.moa.backend.domain.project.entity.ProjectLifecycleStatus.ENDED
                and p.resultStatus = com.moa.backend.domain.project.entity.ProjectResultStatus.SUCCESS
              )
          )
        """)
    Page<Project> findMakerPublicProjects(@Param("makerId") Long makerId, Pageable pageable);

    /**
     * 한글 설명: 서포터(사용자)의 관심 카테고리 기반으로 추천할 프로젝트 조회.
     * - 카테고리는 온보딩 Step1에서 선택한 값(JSON) 기준.
     * - 대소문자 섞인 입력을 방지하기 위해 UPPER()로 비교한다.
     * - 현재 LIVE 상태이면서 심사 승인(Approved)된 프로젝트만 추천 대상으로 포함한다.
     */
    @Query("""
        SELECT p
        FROM Project p
        WHERE UPPER(p.category) IN :categories
          AND p.lifecycleStatus = 'LIVE'
          AND p.reviewStatus = 'APPROVED'
    """)
    List<Project> findRecommendedByCategories(@Param("categories") List<String> categories);

    /**
     * 한글 설명: 특정 프로젝트에 대해 실제 결제 완료된 후원(주문) 수를 조회.
     * - 추천 점수(인기도 가중치)에서 사용된다.
     * - OrderStatus 의 PAID 상태만 집계 대상.
     * - 취소된 주문(CANCELED) 및 결제 대기(PENDING)는 제외된다.
     */
    @Query("""
        SELECT COUNT(o.id)
        FROM Order o
        WHERE o.project.id = :projectId
          AND o.status = 'PAID'
    """)
    int countSupporters(@Param("projectId") Long projectId);
}
