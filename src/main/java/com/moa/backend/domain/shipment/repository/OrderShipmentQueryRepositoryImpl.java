package com.moa.backend.domain.shipment.repository;

import com.moa.backend.domain.order.entity.DeliveryStatus;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.entity.OrderStatus;
import com.moa.backend.domain.shipment.dto.ShipmentListItemResponse;
import com.moa.backend.domain.shipment.dto.ShipmentSearchCondition;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * 한글 설명:
 * - 메이커 배송 콘솔에서 사용하는 주문/배송 목록 조회용 커스텀 리포지토리 구현체.
 * - Order 엔티티를 기준으로 필터/검색/정렬을 적용해서 페이지 단위로 조회한다.
 */
@Repository
@RequiredArgsConstructor
public class OrderShipmentQueryRepositoryImpl implements OrderShipmentQueryRepository {

    // 한글 설명: JPA EntityManager 주입 (동적 JPQL 쿼리 작성용)
    private final EntityManager em;

    @Override
    public Page<ShipmentListItemResponse> searchShipments(
            Long projectId,
            ShipmentSearchCondition condition,
            Pageable pageable
    ) {
        // ===========================
        // 1. 기본 JPQL + 파라미터 셋업
        // ===========================
        StringBuilder jpql = new StringBuilder();
        jpql.append("""
            SELECT o
            FROM Order o
            JOIN o.project p
            JOIN o.user u
            LEFT JOIN o.orderItems oi
            LEFT JOIN oi.reward r
            WHERE p.id = :projectId
              AND o.status = :orderStatus
            """);

        Map<String, Object> params = new HashMap<>();
        params.put("projectId", projectId);
        params.put("orderStatus", OrderStatus.PAID); // 한글 설명: 배송 대상은 결제 완료(PAID) 주문만

        // ===========================
        // 2. 필터 조건 동적 추가
        // ===========================
        // 2-1. 배송 상태 필터
        String status = condition.getStatus();
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            // UI에서 READY/SHIPPING/DELIVERED/CONFIRMED/ISSUE 등의 문자열이 들어온다고 가정
            DeliveryStatus deliveryStatus = DeliveryStatus.valueOf(status);
            jpql.append(" AND o.deliveryStatus = :deliveryStatus");
            params.put("deliveryStatus", deliveryStatus);
        }

        // 2-2. 리워드 필터 (특정 리워드를 포함하는 주문만)
        if (condition.getRewardId() != null) {
            jpql.append(" AND r.id = :rewardId");
            params.put("rewardId", condition.getRewardId());
        }

        // 2-3. 검색어 필터 (주문번호 / 서포터명 / 연락처 / 주소)
        if (condition.getSearch() != null && !condition.getSearch().isBlank()) {
            String search = condition.getSearch().trim();

            jpql.append("""
                AND (
                     LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :search, '%'))
                  OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
                  OR LOWER(o.receiverName) LIKE LOWER(CONCAT('%', :search, '%'))
                  OR o.receiverPhone LIKE CONCAT('%', :search, '%')
                  OR LOWER(o.addressLine1) LIKE LOWER(CONCAT('%', :search, '%'))
                  OR LOWER(COALESCE(o.addressLine2, '')) LIKE LOWER(CONCAT('%', :search, '%'))
                )
                """);
            params.put("search", search);
        }

        // ===========================
        // 3. 정렬 조건 동적 추가
        // ===========================
        String sortBy = condition.getSortBy();
        String sortOrder = condition.getSortOrder();

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "orderDate"; // 기본값: 주문일 기준
        }
        if (sortOrder == null || sortOrder.isBlank()) {
            sortOrder = "desc";   // 기본값: 내림차순
        }
        String direction = sortOrder.equalsIgnoreCase("asc") ? "ASC" : "DESC";

        jpql.append(" ORDER BY ");
        switch (sortBy) {
            case "status" -> jpql.append("o.deliveryStatus ");
            case "amount" -> jpql.append("o.totalAmount ");
            case "deliveryDate" -> jpql.append("o.deliveryCompletedAt ");
            case "orderDate" -> jpql.append("o.createdAt ");
            default -> jpql.append("o.createdAt ");
        }
        jpql.append(direction);

        // ===========================
        // 4. 데이터 쿼리 실행 (페이지 내용)
        // ===========================
        TypedQuery<Order> query = em.createQuery(jpql.toString(), Order.class);
        params.forEach(query::setParameter);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Order> orders = query.getResultList();

        // ===========================
        // 5. 카운트 쿼리 (전체 건수)
        // ===========================
        StringBuilder countJpql = new StringBuilder();
        countJpql.append("""
            SELECT COUNT(DISTINCT o)
            FROM Order o
            JOIN o.project p
            JOIN o.user u
            LEFT JOIN o.orderItems oi
            LEFT JOIN oi.reward r
            WHERE p.id = :projectId
              AND o.status = :orderStatus
            """);

        // where 조건은 동일하게 복붙해야 함 → 재사용을 위해 위에서 따로 함수로 빼도 좋음
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            countJpql.append(" AND o.deliveryStatus = :deliveryStatus");
        }
        if (condition.getRewardId() != null) {
            countJpql.append(" AND r.id = :rewardId");
        }
        if (condition.getSearch() != null && !condition.getSearch().isBlank()) {
            countJpql.append("""
                AND (
                     LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :search, '%'))
                  OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
                  OR LOWER(o.receiverName) LIKE LOWER(CONCAT('%', :search, '%'))
                  OR o.receiverPhone LIKE CONCAT('%', :search, '%')
                  OR LOWER(o.addressLine1) LIKE LOWER(CONCAT('%', :search, '%'))
                  OR LOWER(COALESCE(o.addressLine2, '')) LIKE LOWER(CONCAT('%', :search, '%'))
                )
                """);
        }

        TypedQuery<Long> countQuery = em.createQuery(countJpql.toString(), Long.class);
        params.forEach(countQuery::setParameter);
        long total = countQuery.getSingleResult();

        // ===========================
        // 6. Order → ShipmentListItemResponse 매핑
        // ===========================
        List<ShipmentListItemResponse> content = orders.stream()
                .map(ShipmentListItemResponse::fromOrder) // ← 이 메서드는 아래에서 예시로 보여줄게
                .toList();

        return new PageImpl<>(content, pageable, total);
    }
}
