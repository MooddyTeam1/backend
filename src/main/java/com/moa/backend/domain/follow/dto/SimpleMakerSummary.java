package com.moa.backend.domain.follow.dto;

/**
 * 내가 팔로우한 메이커 요약 정보
 */
public record SimpleMakerSummary(
        Long makerId,
        String name,
        String imageUrl
) {}
