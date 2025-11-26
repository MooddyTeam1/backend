package com.moa.backend.domain.order.repository;

import com.moa.backend.domain.order.entity.DeliveryStatus;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.entity.OrderStatus;
import com.moa.backend.domain.project.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 주문 조회/검색을 담당하는 리포지토리.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ===================== 기본 조회 메서드 =====================

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

    // ================== 배송 요약 카드용 카운트 메서드 ==================

    /**
     * 특정 프로젝트에서 주어진 결제 상태(PAID 등)를 가진 주문 개수.
     */
    long countByProjectIdAndStatus(Long projectId, OrderStatus status);

    /**
     * 특정 프로젝트에서 특정 배송 상태(DELIVERED, SHIPPING 등)를 가진 주문 개수.
     */
    long countByProjectIdAndDeliveryStatus(Long projectId, DeliveryStatus deliveryStatus);

    // ================== 자동 구매확정, 잔금 지급 검증 ==================

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

    // ================== 배송 관련 스케줄링 조회 ==================

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

    // ================== 프로젝트별 펀딩 합계 ==================

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

    // ================== 통계 API용 메서드 (기간 / 카테고리 등) ==================

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
     * 기간별 전체 주문 건수 (상태 무관)
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
     * 결과: Object[] {projectId, projectName, makerName, fundingAmount, goalAmount, achievementRate, remainingDays}
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

    /**
     * 특정 프로젝트에서 결제 상태 기준(PAID 등) 고유 서포터 수 집계.
     */
    @Query("""
        SELECT COUNT(DISTINCT o.user.id)
        FROM Order o
        WHERE o.project.id = :projectId
          AND o.status = :status
        """)
    Long countDistinctSupporterByProjectAndStatus(
            @Param("projectId") Long projectId,
            @Param("status") OrderStatus status
    );

    // 한글 설명: 특정 프로젝트의 특정 상태 주문 전체 (통계 계산용)
    List<Order> findByProject_IdAndStatus(Long projectId, OrderStatus status);

    // 한글 설명: 특정 프로젝트의 최근 주문 10개 (recentOrders 섹션)
    List<Order> findTop10ByProject_IdOrderByCreatedAtDesc(Long projectId);

    // 한글 설명: 플랫폼 전체에서 특정 상태(PAID) 주문 (재후원자 비율 계산용)
    List<Order> findByStatus(OrderStatus status);

    // =========================
    // 1) 서포터 수 / 재후원자 관련
    // =========================

    /**
     * 특정 프로젝트를 결제 완료한 서포터 수(중복 제거).
     */
    @Query("""
        SELECT COUNT(DISTINCT o.user.id)
        FROM Order o
        WHERE o.project.id = :projectId
          AND o.status = :status
    """)
    Integer countDistinctSupporterByProjectIdAndStatus(
            @Param("projectId") Long projectId,
            @Param("status") OrderStatus status
    );

    /**
     * 특정 프로젝트를 결제 완료한 서포터들의 ID 목록.
     */
    @Query("""
        SELECT DISTINCT o.user.id
        FROM Order o
        WHERE o.project.id = :projectId
          AND o.status = :status
    """)
    List<Long> findDistinctSupporterIdsByProjectIdAndStatus(
            @Param("projectId") Long projectId,
            @Param("status") OrderStatus status
    );

    /**
     * 한 서포터가 결제 완료한 프로젝트의 개수(중복 제거).
     */
    @Query("""
        SELECT COUNT(DISTINCT o.project.id)
        FROM Order o
        WHERE o.user.id = :supporterId
          AND o.status = :status
    """)
    int countDistinctProjectIdBySupporterIdAndStatus(
            @Param("supporterId") Long supporterId,
            @Param("status") OrderStatus status
    );

    // =========================
    // 2) 일별 신규 서포터 수 / 모금액
    // =========================

    /**
     * 특정 날짜(date)에, 해당 프로젝트에 "첫 결제"를 한 서포터 수를 카운트한다.
     * - 여기서는 createdAt 을 결제 시각으로 취급.
     */
    @Query("""
        SELECT COUNT(DISTINCT o.user.id)
        FROM Order o
        WHERE o.project.id = :projectId
          AND o.status = com.moa.backend.domain.order.entity.OrderStatus.PAID
          AND FUNCTION('DATE', o.createdAt) = :date
          AND o.createdAt = (
              SELECT MIN(o2.createdAt)
              FROM Order o2
              WHERE o2.project.id = :projectId
                AND o2.user.id = o.user.id
                AND o2.status = com.moa.backend.domain.order.entity.OrderStatus.PAID
          )
    """)
    Integer countNewSupportersForProjectOnDate(
            @Param("projectId") Long projectId,
            @Param("date") LocalDate date
    );

    /**
     * 특정 날짜(date)에 결제 완료(PAID)된 주문들의 totalAmount 합계를 반환.
     * - 여기서도 createdAt 을 결제 시각으로 취급.
     */
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.project.id = :projectId
          AND o.status = com.moa.backend.domain.order.entity.OrderStatus.PAID
          AND FUNCTION('DATE', o.createdAt) = :date
    """)
    Long sumPaidAmountForProjectOnDate(
            @Param("projectId") Long projectId,
            @Param("date") LocalDate date
    );

    // =========================
    // 3) 리워드별 판매 통계
    // =========================

    /**
     * 리워드별 판매 수량 및 금액 집계용 Raw 데이터.
     * - Object[] {rewardId, rewardName, salesCount, totalAmount}
     */
    @Query("""
        SELECT 
            r.id AS rewardId,
            r.name AS rewardName,
            SUM(oi.quantity) AS salesCount,
            SUM(oi.subtotal) AS totalAmount
        FROM Order o
        JOIN o.orderItems oi
        JOIN oi.reward r
        WHERE o.project.id = :projectId
          AND o.status = com.moa.backend.domain.order.entity.OrderStatus.PAID
        GROUP BY r.id, r.name
        ORDER BY SUM(oi.quantity) DESC
    """)
    List<Object[]> findRewardSalesStatsByProjectId(@Param("projectId") Long projectId);

    /**
     * 리워드별 판매 수량만 Raw로 가져오기.
     * - Object[] {rewardId, salesCount}
     */
    @Query("""
        SELECT 
            r.id AS rewardId,
            SUM(oi.quantity) AS salesCount
        FROM Order o
        JOIN o.orderItems oi
        JOIN oi.reward r
        WHERE o.project.id = :projectId
          AND o.status = com.moa.backend.domain.order.entity.OrderStatus.PAID
        GROUP BY r.id
    """)
    List<Object[]> findRewardSalesCountRawByProjectId(@Param("projectId") Long projectId);

    /**
     * Raw 결과를 <rewardId, salesCount> 맵으로 변환.
     */
    default Map<Long, Long> findRewardSalesCountMapByProjectId(Long projectId) {
        List<Object[]> rows = findRewardSalesCountRawByProjectId(projectId);
        Map<Long, Long> map = new HashMap<>();
        if (rows == null) {
            return map;
        }
        for (Object[] row : rows) {
            Long rewardId = (Long) row[0];
            Long salesCount = (Long) row[1];
            map.put(rewardId, salesCount != null ? salesCount : 0L);
        }
        return map;
    }

    // =========================
    // 4) 최근 주문 목록 (최신 N개)
    // =========================

    /**
     * 특정 프로젝트의 주문을 최신순으로 조회.
     */
    Page<Order> findByProject_IdOrderByCreatedAtDesc(Long projectId, Pageable pageable);

    @Query(value = """
            SELECT *
            FROM orders o
            WHERE o.project_id = :projectId
            ORDER BY o.created_at DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Order> findRecentOrdersForProject(@Param("projectId") Long projectId,
                                           @Param("limit") int limit);



    /**
     * 한글 설명:
     *  - 메이커 콘솔용 주문 리스트 조회
     *  - paymentStatus, deliveryStatus 가 null이면 해당 조건은 무시
     *  - createdAt DESC 기준 최신 순 정렬
     */
    @Query("""
        SELECT o
        FROM Order o
        WHERE o.project.id = :projectId
          AND (:paymentStatus IS NULL OR o.status = :paymentStatus)
          AND (:deliveryStatus IS NULL OR o.deliveryStatus = :deliveryStatus)
        ORDER BY o.createdAt DESC
        """)
    Page<Order> findOrdersForMakerConsole(
            @Param("projectId") Long projectId,
            @Param("paymentStatus") OrderStatus paymentStatus,
            @Param("deliveryStatus") DeliveryStatus deliveryStatus,
            Pageable pageable
    );

    /**
     * 한글 설명: 특정 프로젝트에서 결제 상태 기준(PAID 등) 고유 서포터 수 집계.
     * - '결제 완료된 후원자 수'를 의미하며, 단순 주문 수가 아닌 실제 구매한 인원을 센다.
     * - 메이커 프로젝트 정보에서 supporterCount(총 후원자 수) 계산 시 사용된다.
     */
    @Query("""
    SELECT COUNT(o.id) 
    FROM Order o 
    WHERE o.project.id = :projectId AND o.status = 'PAID'
""")
    Long countPaidSupporters(Long projectId);
}
