package com.moa.backend.domain.maker.controller;

import com.moa.backend.domain.maker.dto.MakerSettlementRequest;
import com.moa.backend.domain.maker.dto.MakerSettlementResponse;
import com.moa.backend.domain.maker.service.MakerSettlementService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 한글 설명: "나의 메이커 정산 계좌 정보"를 관리하는 REST 컨트롤러.
 * - URL Prefix: /api/profile/me/maker/settlement
 * - 로그인한 메이커(해당 maker owner)만 접근 가능.
 */
@RestController
@RequestMapping("/api/profile/me/maker/settlement")
@RequiredArgsConstructor
@Tag(name = "Maker-Settlement", description = "메이커 정산 계좌 정보 관리")
public class MakerSettlementProfileController {

    private final MakerSettlementService makerSettlementService;

    /**
     * 한글 설명: 나의 메이커 정산 계좌 정보 조회.
     * - 정산 계좌가 없으면 body 가 null 인 200 OK 응답을 반환.
     */
    @GetMapping
    @Operation(summary = "정산 계좌 정보 조회")
    public ResponseEntity<MakerSettlementResponse> getSettlementAccount(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        // 한글 설명: JWT에 담긴 userId 기준으로 자신의 메이커/정산 계좌 조회.
        MakerSettlementResponse response = makerSettlementService.getSettlementAccount(principal.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * 한글 설명: 나의 메이커 정산 계좌 정보 등록/수정(Upsert).
     * - 이미 있으면 UPDATE, 없으면 INSERT.
     */
    @PutMapping
    @Operation(summary = "정산 계좌 정보 등록/수정")
    public ResponseEntity<MakerSettlementResponse> upsertSettlementAccount(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Valid @RequestBody MakerSettlementRequest request
    ) {
        MakerSettlementResponse response = makerSettlementService.upsertSettlementAccount(principal.getId(), request);
        return ResponseEntity.ok(response);
    }

    /**
     * 한글 설명: 나의 메이커 정산 계좌 정보 삭제.
     * - 향후 정산 진행 상태에 따른 삭제 제한 로직은 Service 에서 처리.
     */
    @DeleteMapping
    @Operation(summary = "정산 계좌 정보 삭제")
    public ResponseEntity<Void> deleteSettlementAccount(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        makerSettlementService.deleteSettlementAccount(principal.getId());
        return ResponseEntity.noContent().build();
    }
}
