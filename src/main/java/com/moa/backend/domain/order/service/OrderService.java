package com.moa.backend.domain.order.service;

import com.moa.backend.domain.order.dto.OrderCreateRequest;
import com.moa.backend.domain.order.dto.OrderDetailResponse;
import com.moa.backend.domain.order.dto.OrderPageResponse;
import com.moa.backend.domain.order.dto.OrderSummaryResponse;
import com.moa.backend.domain.order.entity.DeliveryStatus;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.entity.OrderItem;
import com.moa.backend.domain.order.entity.OrderStatus;
import com.moa.backend.domain.inventory.redis.RewardStockRedisRepository;
import com.moa.backend.domain.order.metrics.OrderCreationMetrics;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.payment.entity.Payment;
import com.moa.backend.domain.payment.repository.PaymentRepository;
import com.moa.backend.domain.payment.service.PaymentService;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.reward.entity.Reward;
import com.moa.backend.domain.reward.repository.RewardRepository;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 주문 생성/조회/취소 비즈니스 로직.
 * <p>
 * 재고 차감·검증·Order 조립은 이 클래스가 담당한다.
 * 재고 선차감(Redis Lua)은 {@link OrderRedisFacade} · {@link OrderStockRedisReservationService} 가 담당하고,
 * 이 클래스는 트랜잭션 내 DB 재고 반영·주문 저장에 집중한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private static final DateTimeFormatter ORDER_CODE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final OrderRepository orderRepository;
    private final ProjectRepository projectRepository;
    private final RewardRepository rewardRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final OrderCreationMetrics orderCreationMetrics;
    private final RewardStockRedisRepository rewardStockRedisRepository;

    /**
     * 서포터 주문을 생성하고 상세 응답을 반환한다.
     * <p>
     * 유한 재고는 {@link OrderRedisFacade} 에서 Redis Lua 로 이미 선차감된 뒤 호출된다.
     * 여기서는 DB {@code stock_quantity} 를 동일 수량만큼 줄여 Redis 와 맞춘다.
     */
    @Transactional
    public OrderDetailResponse createOrder(Long userId, OrderCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));
        if (project.getLifecycleStatus() != ProjectLifecycleStatus.LIVE) {
            throw new AppException(ErrorCode.PROJECT_NOT_FUNDING);
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "주문할 리워드를 선택해주세요.");
        }

        Map<Long, OrderCreateRequest.Item> requestedItems = request.getItems().stream()
                .collect(Collectors.toMap(
                        OrderCreateRequest.Item::getRewardId,
                        item -> item,
                        (left, right) -> {
                            int totalQuantity = left.getQuantity() + right.getQuantity();
                            String note = left.getNote() != null ? left.getNote() : right.getNote();
                            return OrderCreateRequest.Item.builder()
                                    .rewardId(left.getRewardId())
                                    .quantity(totalQuantity)
                                    .note(note)
                                    .build();
                        }
                ));

        List<Reward> rewards = rewardRepository.findByProjectIdAndIdIn(
                project.getId(),
                requestedItems.keySet()
        );

        if (rewards.size() != requestedItems.size()) {
            throw new AppException(ErrorCode.REWARD_NOT_FOUND, "선택한 리워드 중 일부를 찾을 수 없습니다.");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        long totalAmount = 0L;

        for (Reward reward : rewards) {
            OrderCreateRequest.Item item = requestedItems.get(reward.getId());
            Integer quantity = item.getQuantity();
            if (reward.getPrice() == null) {
                throw new AppException(ErrorCode.INTERNAL_ERROR, "리워드 금액이 설정되지 않았습니다.");
            }
            reward.decreaseStock(quantity);

            OrderItem orderItem = OrderItem.of(
                    reward,
                    reward.getName(),
                    reward.getPrice(),
                    quantity,
                    item.getNote()
            );
            orderItems.add(orderItem);
            totalAmount += orderItem.getSubtotal();
        }

        orderItems.sort(Comparator.comparing(OrderItem::getRewardName));
        String orderCode = generateOrderCode();
        String orderName = buildOrderName(orderItems);

        Order order = Order.create(
                user,
                project,
                orderCode,
                orderName,
                totalAmount,
                request.getReceiverName(),
                request.getReceiverPhone(),
                request.getAddressLine1(),
                request.getAddressLine2(),
                request.getZipCode()
        );

        orderItems.forEach(order::addItem);

        Order savedOrder = orderRepository.save(order);

        log.info("주문 생성 완료: orderId={}, userId={}, totalAmount={}",
                savedOrder.getId(), userId, totalAmount);
        orderCreationMetrics.recordSuccess();

        return OrderDetailResponse.from(savedOrder);
    }

    /**
     * 주문 취소.
     * <p>
     * 미결제 취소 시 DB {@link Reward#restoreStock} 와 함께 Redis 재고 키가 있으면 {@code INCRBY} 한다.
     * (결제 완료 취소 경로는 재고 복구 없음 — 기존과 동일.)
     */
    @Transactional
    public void cancelOrder(Long userId, Long orderId, String reason) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new AppException(ErrorCode.ALREADY_PROCESSED, "이미 취소된 주문입니다.");
        }
        if (order.getDeliveryStatus() != null && order.getDeliveryStatus() != DeliveryStatus.NONE) {
            throw new AppException(ErrorCode.BUSINESS_CONFLICT, "배송이 시작된 주문은 취소할 수 없습니다.");
        }

        if (order.getStatus() == OrderStatus.PAID) {
            paymentService.cancelByOrder(order, reason);
            return;
        }

        order.getOrderItems().forEach(item -> {
            if (item.getReward() != null) {
                var reward = item.getReward();
                reward.restoreStock(item.getQuantity());
                if (reward.getStockQuantity() != null) {
                    rewardStockRedisRepository.incrementIfPresent(reward.getId(), item.getQuantity());
                }
            }
        });

        order.cancel();
        orderRepository.save(order);

        log.info("주문 취소 완료: orderId={}, userId={}, reason={}", orderId, userId, reason);
    }

    /**
     * 주문 ID와 사용자 ID로 해당 주문의 projectId 를 조회한다.
     * <p>
     * (레거시/외부용) 취소 전 주문 소유·프로젝트 확인에 사용할 수 있다.
     * 존재하지 않는 주문이면 {@link ErrorCode#ORDER_NOT_FOUND} 를 즉시 반환한다.
     */
    @Transactional(readOnly = true)
    public Long resolveProjectIdForOrder(Long userId, Long orderId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .map(order -> order.getProject().getId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
    }

    /**
     * 사용자 소유 주문을 상세 조회한다.
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public OrderDetailResponse getOrder(Long userId, Long orderId) {
        Order order = orderRepository.findWithItemsByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        Payment payment = paymentRepository.findByOrder(order).orElse(null);
        return OrderDetailResponse.from(order, payment);
    }

    /**
     * 사용자 주문 목록을 페이지 단위로 최신순 조회한다.
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public OrderPageResponse getOrders(Long userId, int page, int size) {
        if (page < 0) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "page는 0 이상이어야 합니다.");
        }
        if (size <= 0) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "size는 1 이상이어야 합니다.");
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orderPage = orderRepository.findAllByUserId(userId, pageRequest);

        return OrderPageResponse.fromOrderPage(orderPage);
    }

    /**
     * 날짜 + 랜덤 문자열 기반 주문 코드 생성.
     */
    private String generateOrderCode() {
        String datePart = ORDER_CODE_DATE_FORMAT.format(LocalDate.now());
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + datePart + "-" + randomPart;
    }

    /**
     * 대표 리워드명 기반 주문명 생성.
     */
    private String buildOrderName(List<OrderItem> orderItems) {
        if (orderItems.isEmpty()) {
            return "주문";
        }
        OrderItem first = orderItems.get(0);
        int extraCount = orderItems.size() - 1;
        if (extraCount <= 0) {
            return first.getRewardName();
        }
        return first.getRewardName() + " 외 " + extraCount + "건";
    }
}
