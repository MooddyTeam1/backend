package com.moa.backend.domain.settlement.repository;

import com.moa.backend.domain.settlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 정산 엔티티 CRUD를 담당하는 리포지토리.
 * (추후 상태별 조회 메서드 추가 가능)
 */
public interface SettlementRepository extends JpaRepository<Settlement, Long> {
}

