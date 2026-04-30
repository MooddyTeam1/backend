package com.moa.backend.domain.order.service;

import com.moa.backend.domain.inventory.redis.RewardStockRedisRepository;
import com.moa.backend.domain.order.dto.OrderCreateRequest;
import com.moa.backend.domain.order.dto.ReservedStockLine;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.reward.entity.Reward;
import com.moa.backend.domain.reward.repository.RewardRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 주문 생성 전 Redis Lua 로 재고를 원자적으로 선차감한다.
 * {@link OrderService#createOrder} 와 동일한 사전 검증(프로젝트·리워드·수량 병합)을 수행한다.
 */
@Service
@RequiredArgsConstructor
public class OrderStockRedisReservationService {

    private final ProjectRepository projectRepository;
    private final RewardRepository rewardRepository;
    private final RewardStockRedisRepository rewardStockRedisRepository;

    /**
     * 유한 재고 리워드에 대해 Redis 선차감을 수행하고, 보상 시 사용할 {@link ReservedStockLine} 목록을 반환한다.
     * 무제한 재고만 있으면 빈 목록을 반환한다.
     */
    @Transactional(readOnly = true)
    public List<ReservedStockLine> reserveStocksForCreate(OrderCreateRequest request) {
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

        List<Reward> sorted = rewards.stream()
                .sorted(Comparator.comparing(Reward::getId))
                .toList();

        List<ReservedStockLine> finiteLines = new ArrayList<>();
        for (Reward reward : sorted) {
            OrderCreateRequest.Item item = requestedItems.get(reward.getId());
            int quantity = item.getQuantity();
            if (quantity <= 0) {
                throw new AppException(ErrorCode.VALIDATION_FAILED, "수량은 1 이상이어야 합니다.");
            }
            if (reward.getPrice() == null) {
                throw new AppException(ErrorCode.INTERNAL_ERROR, "리워드 금액이 설정되지 않았습니다.");
            }
            if (reward.getStockQuantity() != null) {
                rewardStockRedisRepository.ensureKeyFromDbSnapshot(reward.getId(), reward.getStockQuantity());
                finiteLines.add(new ReservedStockLine(reward.getId(), quantity));
            }
        }

        if (finiteLines.isEmpty()) {
            return List.of();
        }

        long luaResult = rewardStockRedisRepository.tryDecrementAllOrNothing(finiteLines);
        if (luaResult != 0L) {
            throw new AppException(ErrorCode.BUSINESS_CONFLICT, "리워드 재고가 부족합니다.");
        }

        return finiteLines;
    }
}
