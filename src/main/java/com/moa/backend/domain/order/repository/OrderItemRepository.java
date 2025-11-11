package com.moa.backend.domain.order.repository;

import com.moa.backend.domain.order.entity.OrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 주문 아이템 전용 리포지토리.
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /** 특정 주문에 속한 아이템 전체 조회 */
    List<OrderItem> findAllByOrderId(Long orderId);
}

