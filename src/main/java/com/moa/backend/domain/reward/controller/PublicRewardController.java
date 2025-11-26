package com.moa.backend.domain.reward.controller;

import java.util.List;

import com.moa.backend.domain.reward.dto.RewardResponse;
import com.moa.backend.domain.reward.service.RewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/projects")
@Tag(name = "Public Reward", description = "공개 프로젝트 리워드 조회 API (프로젝트 상세 화면)")
public class PublicRewardController {

    private final RewardService rewardService;

    @GetMapping("/{projectId}/rewards")
    @Operation(summary = "프로젝트 리워드 + 정보고시 목록 조회")
    public ResponseEntity<List<RewardResponse>> getRewardsWithDisclosure(@PathVariable Long projectId) {
        List<RewardResponse> rewards = rewardService.getRewardsWithDisclosureByProjectId(projectId);
        return ResponseEntity.ok(rewards);
    }
}
