package com.moa.backend.domain.tracking.repository;

import com.moa.backend.domain.tracking.entity.TrackingEvent;
import com.moa.backend.domain.tracking.entity.TrackingEventType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 한글 설명:
 * - 프로젝트 뷰/트래픽 관련 집계를 위한 TrackingEvent 리포지토리.
 * - ProjectTrafficQueryService, MakerProjectManageService 에서 사용되는 메서드들을 모두 포함한다.
 */
public interface TrackingEventRepository extends JpaRepository<TrackingEvent, Long> {

    // =====================================================================
    // 1) 메이커 프로젝트 상세 화면용 통계 메서드
    // =====================================================================

    /**
     * 한글 설명:
     * - 특정 프로젝트에 대해, 주어진 기간(from~to) 동안
     *   특정 이벤트 타입(PROJECT_VIEW 등)의 고유 세션 수를 카운트한다.
     * - MakerProjectManageService.calculateTodayViews(), buildDailyStats() 에서 사용.
     */
    @Query("""
        SELECT COUNT(DISTINCT te.sessionId)
        FROM TrackingEvent te
        WHERE te.project.id = :projectId
          AND te.eventType = :eventType
          AND te.occurredAt BETWEEN :from AND :to
        """)
    Long countDistinctSessionIdByProjectIdAndEventTypeAndOccurredAtBetween(
            @Param("projectId") Long projectId,
            @Param("eventType") TrackingEventType eventType,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    /**
     * 한글 설명:
     * - 특정 프로젝트에 대해 전체 기간 기준
     *   특정 이벤트 타입(PROJECT_VIEW 등)의 고유 세션 수를 카운트한다.
     * - MakerProjectManageService.calculateTotalViews() 에서 사용.
     */
    @Query("""
        SELECT COUNT(DISTINCT te.sessionId)
        FROM TrackingEvent te
        WHERE te.project.id = :projectId
          AND te.eventType = :eventType
        """)
    Long countDistinctSessionIdByProjectIdAndEventType(
            @Param("projectId") Long projectId,
            @Param("eventType") TrackingEventType eventType
    );

    /**
     * 한글 설명:
     * - 특정 프로젝트 + 이벤트 타입(PROJECT_VIEW)의 이벤트 전체 조회.
     * - 채널별 유입 통계(buildChannelStats)에서 사용.
     */
    List<TrackingEvent> findByProject_IdAndEventType(
            Long projectId,
            TrackingEventType eventType
    );

    // =====================================================================
    // 2) 트렌딩/홈 화면용 통계 메서드
    // =====================================================================

    /**
     * 한글 설명:
     * - 특정 프로젝트에 대해, 특정 시각 이후에 발생한 PROJECT_VIEW 개수를 카운트한다.
     * - ProjectTrafficQueryService.getTrendingProjectsWithScore() 에서
     *   recentViewCount 계산에 사용.
     *
     * 메서드 이름 규칙:
     *  - project.id 를 따라가기 위해 Project_Id 를 사용한다.
     */
    Long countByProject_IdAndEventTypeAndOccurredAtAfter(
            Long projectId,
            TrackingEventType eventType,
            LocalDateTime occurredAt
    );

    /**
     * 한글 설명:
     * - 주어진 기간(startDateTime~endDateTime) 동안,
     *   특정 이벤트 타입(PROJECT_VIEW)의 고유 세션 수가 많은
     *   프로젝트 상위 N개를 조회한다.
     *
     * 반환 Object[] 구조:
     *   [0] Long projectId
     *   [1] Long viewCount (distinct sessionId 개수)
     *
     * - ProjectTrafficQueryService.getMostViewedProjects() 에서 사용.
     *   서비스 쪽에서 row[0] = projectId, row[1] = viewCount 로 쓰고 있으니
     *   SELECT 두 컬럼만 반환하는 형태로 맞춘다.
     */
    @Query("""
        SELECT te.project.id AS projectId,
               COUNT(DISTINCT te.sessionId) AS viewCount
        FROM TrackingEvent te
        WHERE te.eventType = :eventType
          AND te.occurredAt BETWEEN :startDateTime AND :endDateTime
        GROUP BY te.project.id
        ORDER BY COUNT(DISTINCT te.sessionId) DESC
        """)
    List<Object[]> findTopProjectsByEventTypeAndPeriod(
            @Param("eventType") TrackingEventType eventType,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable
    );
}
