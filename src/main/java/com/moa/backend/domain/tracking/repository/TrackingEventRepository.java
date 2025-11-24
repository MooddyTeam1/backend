///////////////////////////////////////////////////////////////////////////////
// 5. 이벤트 레포지토리
//    파일: com/moa/backend/domain/tracking/repository/TrackingEventRepository.java
///////////////////////////////////////////////////////////////////////////////

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
 * 한글 설명: 트래킹 이벤트 조회용 레포지토리
 */
public interface TrackingEventRepository extends JpaRepository<TrackingEvent, Long> {

    /**
     * 한글 설명: 특정 기간 동안, 프로젝트별 이벤트 개수를 집계해서
     * "가장 많이 발생한 프로젝트" 순으로 가져오는 쿼리.
     *
     * 사용 예: "최근 1시간 PROJECT_VIEW 상위 N개" = 지금 많이 보는 프로젝트
     */
    @Query("""
        SELECT e.project.id AS projectId,
               COUNT(e.id)   AS eventCount
        FROM TrackingEvent e
        WHERE e.eventType = :eventType
          AND e.project IS NOT NULL
          AND e.occurredAt BETWEEN :from AND :to
        GROUP BY e.project.id
        ORDER BY eventCount DESC
        """)
    List<Object[]> findTopProjectsByEventTypeAndPeriod(
            @Param("eventType") TrackingEventType eventType,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    /**
     * 한글 설명: 특정 프로젝트에 대해, 기준 시각 이후의 이벤트 개수를 카운트
     * - 트렌딩 점수 계산시 "최근 24시간 조회수" 용도로 사용
     */
    Long countByProject_IdAndEventTypeAndOccurredAtAfter(
            Long projectId,
            TrackingEventType eventType,
            LocalDateTime occurredAtAfter
    );
}