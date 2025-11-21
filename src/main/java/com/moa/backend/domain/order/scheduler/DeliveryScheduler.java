package com.moa.backend.domain.order.scheduler;

import com.moa.backend.domain.order.entity.DeliveryStatus;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeliveryScheduler {

    private final OrderRepository orderRepository;

    /**
     * 배송예정일이 오늘인 주문을 PREPARING 상태로 전환.
     */
    @Scheduled(cron = "0 30 2 * * *")
    @Transactional
    public void updateDeliveryPreparing() {
        LocalDate today = LocalDate.now();

        List<Order> targets = orderRepository.findOrdersToPrepare(today);

        targets.forEach(order -> {
            order.setDeliveryStatus(DeliveryStatus.PREPARING);
        });

        orderRepository.saveAll(targets);
    }

    /**
     * 배송예정일 + 1일이 된 주문을 SHIPPING 상태로 전환.
     */
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void updateDeliverySHIPPING() {
        LocalDate shippingDate = LocalDate.now().minusDays(1);

        List<Order> targets = orderRepository.findOrdersToShipping(shippingDate);

        targets.forEach(order -> {
            order.setDeliveryStatus(DeliveryStatus.SHIPPING);
            order.setDeliveryStartedAt(LocalDateTime.now());
        });

        orderRepository.saveAll(targets);
    }

    /**
     * 배송중 시작일로부터 3일 지난 주문을 DELIVERED 상태로 전환.
     */
    @Scheduled(cron = "0 0 16 * * *")
    @Transactional
    public void updateDelivered() {
        LocalDateTime deliveryDate = LocalDateTime.now().minusDays(3);

        List<Order> targets = orderRepository.findOrdersToDelivered(deliveryDate);

        targets.forEach(order -> {
            order.setDeliveryStatus(DeliveryStatus.DELIVERED);
            order.setDeliveryCompletedAt(LocalDateTime.now());
        });

        orderRepository.saveAll(targets);
    }
}
