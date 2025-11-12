package com.moa.backend.domain.follow.dto;

/**
 * 내가 팔로우한 서포터 요약 정보
 */
public record SimpleSupporterSummary(
        Long supporterUserId,  // supporter_profiles.user_id
        String displayName,
        String imageUrl
) {}
