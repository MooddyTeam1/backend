package com.moa.backend.domain.order.service;

import com.moa.backend.domain.order.dto.request.OrderCreateRequest;
import com.moa.backend.domain.order.dto.response.OrderDetailResponse;
import com.moa.backend.domain.order.dto.response.OrderSummaryResponse;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.entity.OrderItem;
import com.moa.backend.domain.order.entity.OrderStatus;
import com.moa.backend.domain.order.repository.OrderRepository;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private static final DateTimeFormatter ORDER_CODE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final OrderRepository orderRepository;
    private final ProjectRepository projectRepository;
    private final RewardRepository rewardRepository;
    private final UserRepository userRepository;

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

        boolean alreadyPaid = orderRepository.existsByProjectIdAndUserIdAndStatus(project.getId(), userId, OrderStatus.PAID);
        if (alreadyPaid) {
            throw new AppException(ErrorCode.ORDER_ALREADY_EXISTS, "해당 프로젝트에 이미 결제 완료한 주문이 있습니다.");
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

    @Transactional(Transactional.TxType.SUPPORTS)
    public OrderDetailResponse getOrder(Long userId, Long orderId) {
        Order order = orderRepository.findWithItemsByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        return OrderDetailResponse.from(order);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<OrderSummaryResponse> getOrders(Long userId) {
        return orderRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(OrderSummaryResponse::from)
                .collect(Collectors.toList());
    }

    private String generateOrderCode() {
        String datePart = ORDER_CODE_DATE_FORMAT.format(LocalDate.now());
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + datePart + "-" + randomPart;
    }

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
