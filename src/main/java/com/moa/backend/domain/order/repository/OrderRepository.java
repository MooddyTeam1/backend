package com.moa.backend.domain.order.repository;

import com.moa.backend.domain.order.entity.DeliveryStatus;
import com.moa.backend.domain.order.entity.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 주문 조회/검색을 담당하는 리포지토리.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /** 주문 코드로 단건 조회 */
    Optional<Order> findByOrderCode(String orderCode);

    /** 주문 + 아이템 정보를 한 번에 로딩 */
    @EntityGraph(attributePaths = {"orderItems", "orderItems.reward"})
    Optional<Order> findWithItemsByIdAndUserId(Long id, Long userId);

    /** 특정 사용자의 주문 목록(최신순) */
    List<Order> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    /** 사용자 소유 주문 여부 확인 */
    Optional<Order> findByIdAndUserId(Long id, Long userId);

    /** 프로젝트 기준 배송 상태별 주문 조회 */
    List<Order> findAllByProjectIdAndDeliveryStatus(Long projectId, DeliveryStatus deliveryStatus);
}

