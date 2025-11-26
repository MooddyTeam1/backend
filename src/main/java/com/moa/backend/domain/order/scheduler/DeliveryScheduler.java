package com.moa.backend.domain.order.scheduler;

import com.moa.backend.domain.notification.entity.NotificationTargetType;
import com.moa.backend.domain.notification.entity.NotificationType;
import com.moa.backend.domain.notification.service.NotificationService;
import com.moa.backend.domain.order.entity.DeliveryStatus;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.repository.OrderRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 한글 설명:
 * - 리워드 배송을 자동으로 단계 전환해주는 스케줄러.
 * - 1) 배송 준비중 전환 (PREPARING)
 * - 2) 배송중 전환 (SHIPPING)
 * - 3) 발송 후 N일 경과 시 자동 배송완료 (DELIVERED)
 *
 * ⚠ CONFIRMED(서포터 수령확정) 상태는 여기서 건드리지 않는다.
 */
@Component
@RequiredArgsConstructor
public class DeliveryScheduler {

    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    /**
     * 한글 설명:
     * - 배송예정일이 오늘인 주문을 PREPARING 상태로 전환한다.
     */
    @Scheduled(cron = "0 30 2 * * *")
    @Transactional
    public void updateDeliveryPreparing() {
        LocalDate today = LocalDate.now();

        List<Order> targets = orderRepository.findOrdersToPrepare(today);

        targets.forEach(order -> {
            order.setDeliveryStatus(DeliveryStatus.PREPARING);

            Long receiverId = order.getUser().getId();
            notificationService.send(
                    receiverId,
                    "배송 준비중",
                    "주문하신 리워드가 곧 출고될 예정입니다.",
                    NotificationType.SUPPORTER,
                    NotificationTargetType.ORDER,
                    order.getId()
            );
        });

        orderRepository.saveAll(targets);
    }

    /**
     * 한글 설명:
     * - 배송예정일 + 1일이 된 주문을 SHIPPING 상태로 전환한다.
     */
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void updateDeliverySHIPPING() {
        LocalDate shippingDate = LocalDate.now().minusDays(1);

        List<Order> targets = orderRepository.findOrdersToShipping(shippingDate);

        targets.forEach(order -> {
            order.setDeliveryStatus(DeliveryStatus.SHIPPING);
            if (order.getDeliveryStartedAt() == null) {
                order.setDeliveryStartedAt(LocalDateTime.now());
            }

            Long receiverId = order.getUser().getId();
            notificationService.send(
                    receiverId,
                    "배송 출발",
                    "주문하신 리워드가 출발했습니다. 수령 후 '수령 완료' 버튼을 눌러주세요.",
                    NotificationType.SUPPORTER,
                    NotificationTargetType.ORDER,
                    order.getId()
            );
        });

        orderRepository.saveAll(targets);
    }

    /**
     * 한글 설명:
     * - 배송중(배송 시작) 이후 5일이 지난 주문을 자동으로 DELIVERED 상태로 전환한다.
     */
    @Scheduled(cron = "0 0 16 * * *")
    @Transactional
    public void updateDelivered() {
        LocalDateTime autoCompleteBaseTime = LocalDateTime.now().minusDays(5);

        List<Order> targets = orderRepository.findOrdersToDelivered(autoCompleteBaseTime);

        targets.forEach(order -> {
            order.completeDelivery();

            Long receiverId = order.getUser().getId();
            notificationService.send(
                    receiverId,
                    "배송 완료",
                    "주문하신 리워드가 도착한 것으로 확인되어 배송 완료 처리되었습니다.",
                    NotificationType.SUPPORTER,
                    NotificationTargetType.ORDER,
                    order.getId()
            );
        });

        orderRepository.saveAll(targets);
    }
}
