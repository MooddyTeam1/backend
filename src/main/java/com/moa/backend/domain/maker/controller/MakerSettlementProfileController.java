package com.moa.backend.domain.maker.controller;

import com.moa.backend.domain.maker.dto.MakerSettlementRequest;
import com.moa.backend.domain.maker.dto.MakerSettlementResponse;
import com.moa.backend.domain.maker.service.MakerSettlementService;
import com.moa.backend.global.error.ErrorResponse;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "정산 계좌 정보 조회", description = "로그인한 메이커의 정산 계좌 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정산 계좌 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "메이커 또는 정산 정보를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @Operation(summary = "정산 계좌 정보 등록/수정", description = "정산 계좌를 신규 등록하거나 기존 정보를 수정합니다. 계좌번호 형식이 유효하지 않으면 저장할 수 없습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정산 계좌 저장 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @Operation(summary = "정산 계좌 정보 삭제", description = "정산 계좌 정보를 삭제합니다. 정산 처리 중 계좌는 서비스 정책에 따라 삭제가 거부될 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "정산 계좌 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "진행 중 정산으로 인해 삭제 불가", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteSettlementAccount(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        makerSettlementService.deleteSettlementAccount(principal.getId());
        return ResponseEntity.noContent().build();
    }
}
