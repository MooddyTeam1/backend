package com.moa.backend.domain.order.repository;

import com.moa.backend.domain.order.entity.DeliveryStatus;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.entity.OrderStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderCode(String orderCode);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.reward"})
    Optional<Order> findWithItemsByIdAndUserId(Long id, Long userId);

    List<Order> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Order> findByIdAndUserId(Long id, Long userId);

    boolean existsByProjectIdAndUserIdAndStatus(Long projectId, Long userId, OrderStatus status);

    List<Order> findAllByProjectIdAndDeliveryStatus(Long projectId, DeliveryStatus deliveryStatus);
}

