package com.mooddy.backend.feature.playlist.dto;

import com.mooddy.backend.feature.playlist.domain.Visibility;

/**
 * 플레이리스트 Fork 요청 DTO
 * - PUBLIC 또는 PRIVATE만 선택 가능
 * - SHARED는 Fork 후 별도 수정으로 처리
 */
public record PlaylistForkRequestDto(
        Visibility visibility  // nullable, 기본값: PUBLIC
) {
}
