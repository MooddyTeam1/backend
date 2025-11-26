package com.moa.backend.domain.maker.repository;

import com.moa.backend.domain.maker.entity.MakerSettlementProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 한글 설명: 메이커 정산 계좌 정보를 조회/저장하기 위한 JPA 레포지토리.
 */
@Repository
public interface MakerSettlementProfileRepository extends JpaRepository<MakerSettlementProfile, Long> {

    /**
     * 한글 설명: 메이커 ID로 정산 계좌 정보를 조회.
     * - MakerSettlementProfile.maker.id 기준.
     */
    Optional<MakerSettlementProfile> findByMaker_Id(Long makerId);
}
