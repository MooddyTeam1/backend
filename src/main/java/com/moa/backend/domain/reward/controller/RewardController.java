package com.moa.backend.domain.reward.controller;

import com.moa.backend.domain.reward.dto.RewardStockIncreaseRequest;
import com.moa.backend.domain.reward.dto.RewardStockIncreaseResponse;
import com.moa.backend.domain.reward.service.RewardService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reward")
@RequiredArgsConstructor
@Tag(name = "Reward", description = "리워드 재고/설정 (메이커/관리자)")
public class RewardController {

    private final RewardService rewardService;

    /**
     * 리워드 수량 추가
     * JWT 인증된 MAKER/ADMIN만
     */
    @PreAuthorize("hasRole('MAKER') or hasRole('ADMIN')")
    @PatchMapping("/{rewardId}/stock/increase")
    @Operation(summary = "리워드 재고 추가")
    public ResponseEntity<RewardStockIncreaseResponse> increaseStock(
            @Parameter(example = "1300") @PathVariable Long rewardId,
            @Valid @RequestBody RewardStockIncreaseRequest request,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        RewardStockIncreaseResponse response =
                rewardService.increaseStock(rewardId, request, principal.getId());

        return ResponseEntity.ok(response);
    }
}
