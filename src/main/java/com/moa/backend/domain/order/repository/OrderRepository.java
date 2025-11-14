package com.moa.backend.domain.order.repository;

import com.moa.backend.domain.order.entity.DeliveryStatus;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
     * 특정 사용자의 주문 목록(최신순)
     */
    List<Order> findAllByUserIdOrderByCreatedAtDesc(Long userId);

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

    /**
     * 배송 완료 후 일정 시간이 지난 주문 조회(자동 구매확정 대상).
     */
    List<Order> findAllByDeliveryStatusAndDeliveryCompletedAtBefore(
            DeliveryStatus deliveryStatus,
            java.time.LocalDateTime deliveryCompletedAt
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
}
