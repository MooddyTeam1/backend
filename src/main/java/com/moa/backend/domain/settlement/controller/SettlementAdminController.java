package com.moa.backend.domain.settlement.controller;

import com.moa.backend.domain.settlement.dto.SettlementResponse;
import com.moa.backend.domain.settlement.entity.Settlement;
import com.moa.backend.domain.settlement.repository.SettlementRepository;
import com.moa.backend.domain.settlement.service.SettlementService;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자용 정산 수동 처리 API.
 * 정산 생성, 선지급, 잔금 지급을 확인할 수 있다.
 */
@RestController
@RequestMapping("/api/admin/settlements")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class SettlementAdminController {

    private final SettlementService settlementService;
    private final SettlementRepository settlementRepository;

    /**
     * 특정 프로젝트의 정산 정보를 새로 생성한다.
     */
    @PostMapping("/{projectId}")
    public ResponseEntity<SettlementResponse> create(@PathVariable Long projectId) {
        Settlement settlement = settlementService.createSettlement(projectId);
        return ResponseEntity.ok(SettlementResponse.from(settlement));
    }

    /**
     * 선지급(First Payout)을 수동으로 실행한다.
     */
    @PostMapping("/{settlementId}/first-payout")
    public ResponseEntity<SettlementResponse> payFirst(@PathVariable Long settlementId) {
        Settlement settlement = settlementService.payFirstPayout(settlementId);
        return ResponseEntity.ok(SettlementResponse.from(settlement));
    }

    /**
     * 잔금(Final Payout)을 수동으로 실행한다.
     */
    @PostMapping("/{settlementId}/final-payout")
    public ResponseEntity<SettlementResponse> payFinal(@PathVariable Long settlementId) {
        Settlement settlement = settlementService.payFinalPayout(settlementId);
        return ResponseEntity.ok(SettlementResponse.from(settlement));
    }

    /**
     * 잔금 준비 상태(FINAL_READY)로 수동으로 전환한다.
     */
    @PostMapping("/{settlementId}/final-ready")
    public ResponseEntity<SettlementResponse> markFinalReady(@PathVariable Long settlementId) {
        Settlement settlement = settlementService.markFinalReady(settlementId);
        return ResponseEntity.ok(SettlementResponse.from(settlement));
    }

    /**
     * 정산 단건 상세를 조회한다.
     */
    @GetMapping("/{settlementId}")
    public ResponseEntity<SettlementResponse> get(@PathVariable Long settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new AppException(ErrorCode.SETTLEMENT_NOT_FOUND));
        return ResponseEntity.ok(SettlementResponse.from(settlement));
    }
}
