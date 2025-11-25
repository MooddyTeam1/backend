package com.moa.backend.domain.settlement.controller;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.settlement.dto.MakerSettlementListItemResponse;
import com.moa.backend.domain.settlement.dto.SettlementResponse;
import com.moa.backend.domain.settlement.entity.Settlement;
import com.moa.backend.domain.settlement.repository.SettlementRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 메이커용 정산 조회 API (조회 전용)
 */
@RestController
@RequestMapping("/api/maker/settlements")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MAKER') or hasRole('ADMIN')")
@Tag(name = "Settlement-Maker", description = "메이커 정산 조회(목록/상세)")
public class MakerSettlementController {

    private final SettlementRepository settlementRepository;
    private final MakerRepository makerRepository;

    /**
     * 정산 목록(페이지네이션) 조회 - 현재 로그인한 메이커 소유자 기준
     */
    @GetMapping
    @Operation(summary = "메이커 정산 목록 조회 (페이지네이션)")
    public ResponseEntity<Page<MakerSettlementListItemResponse>> list(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(description = "0부터 시작하는 페이지", example = "0") @RequestParam(required = false) Integer page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(required = false) Integer size
    ) {
        Maker maker = makerRepository.findByOwner_Id(principal.getId())
                .orElseThrow(() -> new AppException(ErrorCode.MAKER_NOT_FOUND));

        int pageNumber = (page == null || page < 0) ? 0 : page;
        int pageSize = (size == null || size <= 0) ? 20 : size;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<MakerSettlementListItemResponse> result = settlementRepository
                .findByMaker_Id(maker.getId(), pageable)
                .map(MakerSettlementListItemResponse::from);

        return ResponseEntity.ok(result);
    }

    /**
     * 정산 상세 조회 - 소유자 검증
     */
    @GetMapping("/{settlementId}")
    @Operation(summary = "메이커 정산 상세 조회")
    public ResponseEntity<SettlementResponse> detail(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1601") @PathVariable Long settlementId
    ) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new AppException(ErrorCode.SETTLEMENT_NOT_FOUND));

        // 소유자 검증: ADMIN은 통과, MAKER는 본인 소유만
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            Long ownerUserId = settlement.getMaker().getOwner().getId();
            if (!ownerUserId.equals(principal.getId())) {
                throw new AppException(ErrorCode.ACCESS_DENIED);
            }
        }

        return ResponseEntity.ok(SettlementResponse.from(settlement));
    }
}
