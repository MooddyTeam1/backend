package com.moa.backend.domain.order.repository;

import com.moa.backend.domain.order.entity.DeliveryStatus;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.entity.OrderStatus;
import com.moa.backend.domain.project.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 주문 조회/검색을 담당하는 리포지토리.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 주문 코드로 단건 조회
     */
    Optional<Order> findByOrderCode(String orderCode);

    /**
     * 주문 + 아이템 정보를 한 번에 로딩
     */
    @EntityGraph(attributePaths = {"orderItems", "orderItems.reward"})
    Optional<Order> findWithItemsByIdAndUserId(Long id, Long userId);

    /**
     * 특정 사용자의 주문 목록(페이지네이션)
     */
    Page<Order> findAllByUserId(Long userId, Pageable pageable);

    /**
     * 사용자 소유 주문 여부 확인
     */
    Optional<Order> findByIdAndUserId(Long id, Long userId);

    /**
     * 프로젝트 기준 배송 상태별 주문 조회
     */
    List<Order> findAllByProjectIdAndDeliveryStatus(Long projectId, DeliveryStatus deliveryStatus);

    /**
     * 프로젝트와 주문 상태로 주문 목록 조회
     */
    List<Order> findAllByProjectIdAndStatus(Long projectId, OrderStatus status);

    // ================== 👇 추가: 배송 요약 카드용 카운트 메서드 ==================

    /**
     * 한글 설명: 특정 프로젝트에서 주어진 결제 상태(PAID 등)를 가진 주문 개수.
     * - 배송 요약 카드의 "총 주문 수" 계산에 사용 (보통 PAID 기준).
     */
    long countByProjectIdAndStatus(Long projectId, OrderStatus status);

    /**
     * 한글 설명: 특정 프로젝트에서 특정 배송 상태(DELIVERED, SHIPPING 등)를 가진 주문 개수.
     * - 배송 요약 카드의 "배송 준비중 / 배송중 / 배송 완료 / 문제" 카운트에 사용.
     */
    long countByProjectIdAndDeliveryStatus(Long projectId, DeliveryStatus deliveryStatus);

    // ========================================================================

    /**
     * 배송 완료 후 일정 시간이 지난 주문 조회(자동 구매확정 대상).
     */
    List<Order> findAllByDeliveryStatusAndDeliveryCompletedAtBefore(
            DeliveryStatus deliveryStatus,
            LocalDateTime deliveryCompletedAt
    );

    /**
     * 프로젝트/주문 상태 조건으로 총 주문금액 합산
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o " +
            "WHERE o.project.id = :projectId AND o.status = :status")
    Optional<Long> sumTotalAmountByProjectIdAndStatus(
            @Param("projectId") Long projectId,
            @Param("status") OrderStatus status
    );

    /**
     * 지정된 배송 상태가 아닌 주문이 남아있는지 여부(잔금 지급 전 검증)
     */
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
            "FROM Order o WHERE o.project.id = :projectId " +
            "AND o.deliveryStatus <> :status")
    boolean existsByProjectIdAndDeliveryStatusNot(
            @Param("projectId") Long projectId,
            @Param("status") DeliveryStatus status
    );

    /**
     * 배송 예정일이 오늘인 주문 목록 조회.
     */
    @Query("""
            SELECT DISTINCT o FROM Order o
            JOIN o.orderItems oi
            JOIN oi.reward r
            WHERE o.deliveryStatus = 'NONE'
            AND r.estimatedDeliveryDate = :today
            """)
    List<Order> findOrdersToPrepare(LocalDate today);

    /**
     * 배송 준비중인 주문 목록 조회.
     */
    @Query("""
            SELECT DISTINCT o FROM Order o
            JOIN o.orderItems oi
            JOIN oi.reward r
            WHERE o.deliveryStatus = 'PREPARING'
            AND r.estimatedDeliveryDate = :shippingDate
            """)
    List<Order> findOrdersToShipping(LocalDate shippingDate);

    /**
     * 배송 중인 주문 목록 조회.
     */
    @Query("""
            SELECT DISTINCT o FROM Order o
            WHERE o.deliveryStatus = 'SHIPPING'
            AND o.deliveryStartedAt <= :deliveryDate
            """)
    List<Order> findOrdersToDelivered(LocalDateTime deliveryDate);

    /**
     * 특정 프로젝트에 대해 현재까지 결제(지불) 완료된 모금액 총합을 조회한다.
     */
    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0)
            FROM Order o
            WHERE o.project.id = :projectId
            AND o.status = 'PAID'
            """)
    Long getTotalFundedAmount(Long projectId);

    // ========== 통계 API용 메서드 ==========

    /**
     * 기간별 PAID 주문 총액 합계
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE o.status = :status " +
            "AND o.createdAt BETWEEN :startDateTime AND :endDateTime")
    Optional<Long> sumTotalAmountByStatusAndCreatedAtBetween(
            @Param("status") OrderStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    /**
     * 기간별 PAID 주문 총액 합계 (makerId/projectId 필터)
     */
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        JOIN o.project p
        WHERE o.status = :status
          AND o.createdAt BETWEEN :startDateTime AND :endDateTime
          AND (:makerId IS NULL OR p.maker.id = :makerId)
          AND (:projectId IS NULL OR p.id = :projectId)
        """)
    Optional<Long> sumTotalAmountByStatusAndCreatedAtBetweenAndFilters(
            @Param("status") OrderStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("makerId") Long makerId,
            @Param("projectId") Long projectId
    );

    /**
     * 기간별 주문 건수
     */
    Long countByStatusAndCreatedAtBetween(
            OrderStatus status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

    /**
     * 기간별 전체 주문 건수 (상태 무관, 시도 횟수 대용)
     */
    Long countByCreatedAtBetween(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

    /**
     * 전체 기간 활성 서포터 수 (PAID 주문이 있는 고유 사용자 수)
     */
    @Query("SELECT COUNT(DISTINCT o.user.id) FROM Order o WHERE o.status = :status")
    Long countDistinctUserByStatus(@Param("status") OrderStatus status);

    /**
     * 특정 시점 이전의 활성 서포터 수
     */
    @Query("SELECT COUNT(DISTINCT o.user.id) FROM Order o " +
            "WHERE o.status = :status AND o.createdAt < :beforeDateTime")
    Long countDistinctUserByStatusAndCreatedAtBefore(
            @Param("status") OrderStatus status,
            @Param("beforeDateTime") LocalDateTime beforeDateTime
    );

    /**
     * 기간 내 주문이 있는 고유 프로젝트 수 (maker/project 필터)
     */
    @Query("""
        SELECT COUNT(DISTINCT p.id)
        FROM Order o
        JOIN o.project p
        WHERE o.status = :status
          AND o.createdAt BETWEEN :startDateTime AND :endDateTime
          AND (:makerId IS NULL OR p.maker.id = :makerId)
          AND (:projectId IS NULL OR p.id = :projectId)
        """)
    Long countDistinctProjectByStatusAndCreatedAtBetweenAndFilters(
            @Param("status") OrderStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("makerId") Long makerId,
            @Param("projectId") Long projectId
    );

    /**
     * 일별 통계 (날짜, 펀딩액, 주문 건수)
     * 결과: Object[] {날짜(DATE), 총액(LONG), 건수(LONG)}
     */
    @Query("""
        SELECT CAST(o.createdAt AS date) as date, 
               COALESCE(SUM(o.totalAmount), 0) as totalAmount,
               COUNT(o) as orderCount
        FROM Order o
        WHERE o.status = :status
        AND o.createdAt BETWEEN :startDateTime AND :endDateTime
        GROUP BY CAST(o.createdAt AS date)
        ORDER BY CAST(o.createdAt AS date)
        """)
    List<Object[]> findDailyStatsByStatusAndCreatedAtBetween(
            @Param("status") OrderStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    /**
     * 일별 프로젝트 수 (주문이 발생한 고유 프로젝트 기준)
     * 결과: Object[] {날짜(DATE), 프로젝트수(LONG)}
     */
    @Query("""
        SELECT CAST(o.createdAt AS date) as date,
               COUNT(DISTINCT p.id) as projectCount
        FROM Order o
        JOIN o.project p
        WHERE o.status = :status
        AND o.createdAt BETWEEN :startDateTime AND :endDateTime
        GROUP BY CAST(o.createdAt AS date)
        ORDER BY CAST(o.createdAt AS date)
        """)
    List<Object[]> findDailyProjectCountByStatusAndCreatedAtBetween(
            @Param("status") OrderStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    /**
     * 카테고리별 통계 (카테고리, 펀딩액, 프로젝트 수, 주문 건수)
     * 결과: Object[] {카테고리(STRING), 총액(LONG), 프로젝트수(LONG), 주문건수(LONG)}
     */
    @Query("""
            SELECT p.category as category,
                   COALESCE(SUM(o.totalAmount), 0) as totalAmount,
                   COUNT(DISTINCT p.id) as projectCount,
                   COUNT(o) as orderCount
            FROM Order o
            JOIN o.project p
            WHERE o.status = :status
            AND o.createdAt BETWEEN :startDateTime AND :endDateTime
            GROUP BY p.category
            ORDER BY COALESCE(SUM(o.totalAmount), 0) DESC
            """)
    List<Object[]> findCategoryStatsByStatusAndCreatedAtBetween(
            @Param("status") OrderStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    /**
     * 프로젝트별 펀딩 통계 (Top N)
     * 결과: Object[] {프로젝트ID(LONG), 프로젝트명(STRING), 메이커명(STRING),
     * 총펀딩액(LONG), 목표금액(LONG), 달성률(DOUBLE), 남은일수(INT)}
     */
    @Query("""
        SELECT p.id as projectId,
               p.title as projectName,
               m.name as makerName,
               COALESCE(SUM(o.totalAmount), 0) as fundingAmount,
               p.goalAmount as goalAmount,
               (COALESCE(SUM(o.totalAmount), 0) * 100.0 / p.goalAmount) as achievementRate,
               FUNCTION('timestampdiff', DAY, CURRENT_DATE, p.endDate) as remainingDays
        FROM Order o
        JOIN o.project p
        JOIN p.maker m
        WHERE o.status = :status
        AND o.createdAt BETWEEN :startDateTime AND :endDateTime
        GROUP BY p.id, p.title, m.name, p.goalAmount, p.endDate
        ORDER BY COALESCE(SUM(o.totalAmount), 0) DESC
        """)
    List<Object[]> findTopProjectsByFundingAmount(
            @Param("status") OrderStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable
    );

    /**
     * 시간대별 주문 통계 (status 필터 + 옵션 category/maker)
     * 결과: Object[] {hour(INT), count(LONG), amount(LONG)}
     */
    @Query("""
        SELECT FUNCTION('HOUR', o.createdAt) as hour,
               COUNT(o) as orderCount,
               COALESCE(SUM(o.totalAmount), 0) as totalAmount
        FROM Order o
        JOIN o.project p
        WHERE o.createdAt BETWEEN :startDateTime AND :endDateTime
          AND o.status = :status
          AND (:category IS NULL OR p.category = :category)
          AND (:makerId IS NULL OR p.maker.id = :makerId)
        GROUP BY FUNCTION('HOUR', o.createdAt)
        ORDER BY hour
        """)
    List<Object[]> findHourlyStatsByStatusAndFilters(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("status") OrderStatus status,
            @Param("category") Category category,
            @Param("makerId") Long makerId
    );

    /**
     * 프로젝트별 상세 집계 (주문수/펀딩액) - 필터 옵션
     * 결과: Object[] {projectId, projectName, makerName, orderCount, fundingAmount}
     */
    @Query("""
        SELECT p.id,
               p.title,
               m.name,
               COUNT(o) as orderCount,
               COALESCE(SUM(o.totalAmount), 0) as fundingAmount
        FROM Order o
        JOIN o.project p
        JOIN p.maker m
        WHERE o.createdAt BETWEEN :startDateTime AND :endDateTime
          AND o.status = :status
          AND (:category IS NULL OR p.category = :category)
          AND (:makerId IS NULL OR p.maker.id = :makerId)
        GROUP BY p.id, p.title, m.name
        ORDER BY COALESCE(SUM(o.totalAmount), 0) DESC
        """)
    List<Object[]> findProjectDetailsByStatusAndFilters(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("status") OrderStatus status,
            @Param("category") Category category,
            @Param("makerId") Long makerId
    );

    /**
     * 메이커별 상세 집계 (프로젝트수/주문수/펀딩액) - 필터 옵션
     * 결과: Object[] {makerId, makerName, projectCount, orderCount, fundingAmount}
     */
    @Query("""
        SELECT m.id,
               m.name,
               COUNT(DISTINCT p.id) as projectCount,
               COUNT(o) as orderCount,
               COALESCE(SUM(o.totalAmount), 0) as fundingAmount
        FROM Order o
        JOIN o.project p
        JOIN p.maker m
        WHERE o.createdAt BETWEEN :startDateTime AND :endDateTime
          AND o.status = :status
          AND (:category IS NULL OR p.category = :category)
          AND (:makerId IS NULL OR p.maker.id = :makerId)
        GROUP BY m.id, m.name
        ORDER BY COALESCE(SUM(o.totalAmount), 0) DESC
        """)
    List<Object[]> findMakerDetailsByStatusAndFilters(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("status") OrderStatus status,
            @Param("category") Category category,
            @Param("makerId") Long makerId
    );

    /**
     * 수익 리포트 상세 (일자/프로젝트 단위)
     * 결과: Object[] {date(DATE), projectId, projectName, makerName, totalAmount}
     */
    @Query("""
        SELECT CAST(o.createdAt AS date) as date,
               p.id,
               p.title,
               m.name,
               COALESCE(SUM(o.totalAmount), 0) as totalAmount
        FROM Order o
        JOIN o.project p
        JOIN p.maker m
        WHERE o.status = :status
          AND o.createdAt BETWEEN :startDateTime AND :endDateTime
          AND (:makerId IS NULL OR m.id = :makerId)
          AND (:projectId IS NULL OR p.id = :projectId)
        GROUP BY CAST(o.createdAt AS date), p.id, p.title, m.name
        ORDER BY CAST(o.createdAt AS date), COALESCE(SUM(o.totalAmount), 0) DESC
        """)
    List<Object[]> findRevenueDetailsByDateAndFilters(
            @Param("status") OrderStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("makerId") Long makerId,
            @Param("projectId") Long projectId
    );

    /**
     * 월별 KPI용 월간 합계 (fundingAmount, orderCount)
     */
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0) as totalAmount,
               COUNT(o) as orderCount
        FROM Order o
        WHERE o.status = :status
          AND o.createdAt BETWEEN :startDateTime AND :endDateTime
        """)
    List<Object[]> findMonthlyFundingAndCount(
            @Param("status") OrderStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    /**
     * 월별 일자별 통계 (fundingAmount, orderCount, projectCount)
     */
    @Query("""
        SELECT CAST(o.createdAt AS date) as date,
               COALESCE(SUM(o.totalAmount), 0) as totalAmount,
               COUNT(o) as orderCount,
               COUNT(DISTINCT p.id) as projectCount
        FROM Order o
        JOIN o.project p
        WHERE o.status = :status
          AND o.createdAt BETWEEN :startDateTime AND :endDateTime
        GROUP BY CAST(o.createdAt AS date)
        ORDER BY CAST(o.createdAt AS date)
        """)
    List<Object[]> findMonthlyDailyStats(
            @Param("status") OrderStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    /**
     * 기간 내 고유 서포터 수 (PAID 기준)
     */
    @Query("""
        SELECT COUNT(DISTINCT o.user.id)
        FROM Order o
        WHERE o.status = :status
          AND o.createdAt BETWEEN :startDateTime AND :endDateTime
        """)
    Long countDistinctSupporterByStatusAndCreatedAtBetween(
            @Param("status") OrderStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

}
