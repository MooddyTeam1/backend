package com.moa.backend.domain.settlement.controller;

import com.moa.backend.domain.settlement.dto.SettlementResponse;
import com.moa.backend.domain.settlement.entity.Settlement;
import com.moa.backend.domain.settlement.dto.SettlementListItemResponse;
import com.moa.backend.domain.settlement.repository.SettlementRepository;
import com.moa.backend.domain.settlement.service.SettlementService;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 관리자용 정산 수동 처리 API.
 * 정산 생성, 선지급, 잔금 지급을 확인할 수 있다.
 */
@RestController
@RequestMapping("/api/admin/settlements")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Settlement-Admin", description = "정산 생성/선지급/잔금/조회 (ADMIN)")
public class SettlementAdminController {

    private final SettlementService settlementService;
    private final SettlementRepository settlementRepository;

    /**
     * 정산 요약 (상태별 건수/금액)
     */
    @GetMapping("/summary")
    @Operation(summary = "정산 요약 조회")
    public ResponseEntity<com.moa.backend.domain.settlement.dto.SettlementSummaryResponse> summary() {
        return ResponseEntity.ok(settlementService.getSummary());
    }

    /**
     * 정산 목록(페이지네이션) 조회.
     * 기본 정렬: updatedAt DESC.
     */
    @GetMapping
    @Operation(summary = "정산 목록 조회 (페이지네이션)")
    public ResponseEntity<Page<SettlementListItemResponse>> list(
            @Parameter(description = "0부터 시작하는 페이지", example = "0") Integer page,
            @Parameter(description = "페이지 크기", example = "20") Integer size
    ) {
        int pageNumber = (page == null || page < 0) ? 0 : page;
        int pageSize = (size == null || size <= 0) ? 20 : size;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<SettlementListItemResponse> result = settlementRepository.findAll(pageable)
                .map(SettlementListItemResponse::from);
        return ResponseEntity.ok(result);
    }

    /**
     * 특정 프로젝트의 정산 정보를 새로 생성한다.
     */
    @PostMapping("/{projectId}")
    @Operation(summary = "정산 생성", description = "프로젝트 ID로 정산 레코드를 생성합니다.")
    public ResponseEntity<SettlementResponse> create(@Parameter(example = "1200") @PathVariable Long projectId) {
        Settlement settlement = settlementService.createSettlement(projectId);
        return ResponseEntity.ok(SettlementResponse.from(settlement));
    }

    /**
     * 선지급(First Payout)을 수동으로 실행한다.
     */
    @PostMapping("/{settlementId}/first-payout")
    @Operation(summary = "정산 선지급 실행", description = "FIRST_PAYOUT을 수동으로 실행합니다.")
    public ResponseEntity<SettlementResponse> payFirst(@Parameter(example = "1601") @PathVariable Long settlementId) {
        Settlement settlement = settlementService.payFirstPayout(settlementId);
        return ResponseEntity.ok(SettlementResponse.from(settlement));
    }

    /**
     * 잔금(Final Payout)을 수동으로 실행한다.
     */
    @PostMapping("/{settlementId}/final-payout")
    @Operation(summary = "정산 잔금 지급", description = "FINAL_PAYOUT을 수동으로 실행합니다.")
    public ResponseEntity<SettlementResponse> payFinal(@Parameter(example = "1601") @PathVariable Long settlementId) {
        Settlement settlement = settlementService.payFinalPayout(settlementId);
        return ResponseEntity.ok(SettlementResponse.from(settlement));
    }

    /**
     * 잔금 준비 상태(FINAL_READY)로 수동으로 전환한다.
     */
    @PostMapping("/{settlementId}/final-ready")
    @Operation(summary = "잔금 준비 상태 전환", description = "정산을 FINAL_READY 상태로 전환합니다.")
    public ResponseEntity<SettlementResponse> markFinalReady(@Parameter(example = "1601") @PathVariable Long settlementId) {
        Settlement settlement = settlementService.markFinalReady(settlementId);
        return ResponseEntity.ok(SettlementResponse.from(settlement));
    }

    /**
     * 정산 단건 상세를 조회한다.
     */
    @GetMapping("/{settlementId}")
    @Operation(summary = "정산 상세 조회")
    public ResponseEntity<SettlementResponse> get(@Parameter(example = "1601") @PathVariable Long settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new AppException(ErrorCode.SETTLEMENT_NOT_FOUND));
        return ResponseEntity.ok(SettlementResponse.from(settlement));
    }
}
