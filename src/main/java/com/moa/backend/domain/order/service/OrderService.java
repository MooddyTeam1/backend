package com.moa.backend.domain.order.service;

import com.moa.backend.domain.order.dto.OrderCreateRequest;
import com.moa.backend.domain.order.dto.OrderDetailResponse;
import com.moa.backend.domain.order.dto.OrderPageResponse;
import com.moa.backend.domain.order.dto.OrderSummaryResponse;
import com.moa.backend.domain.order.entity.DeliveryStatus;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.entity.OrderItem;
import com.moa.backend.domain.order.entity.OrderStatus;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 주문 생성/조회 비즈니스 로직을 담당한다.
 * 재고 차감, 주문 코드 생성, 배송지/아이템 조립을 처리한다.
 */
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

    /**
     * 서포터 주문을 생성하고 상세 응답을 반환한다.
     */
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

        return OrderDetailResponse.from(savedOrder);
    }

    /**
     * 사용자 소유 주문을 상세 조회한다.
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public OrderDetailResponse getOrder(Long userId, Long orderId) {
        Order order = orderRepository.findWithItemsByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        Payment payment = paymentRepository.findByOrder(order).orElse(null);
        return OrderDetailResponse.from(order, payment);
    }

    /**
     * 사용자 주문 목록을 페이지 단위로 최신순 조회한다.
     */
    @Transactional(Transactional.TxType.SUPPORTS)
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
     * 사용자 주문을 취소한다.
     */
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
                item.getReward().restoreStock(item.getQuantity());
            }
        });

        order.cancel();
        orderRepository.save(order);
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
