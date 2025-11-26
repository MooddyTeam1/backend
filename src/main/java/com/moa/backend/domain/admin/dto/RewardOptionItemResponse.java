package com.moa.backend.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 한글 설명: 개별 리워드 옵션 DTO.
 * - 예: name = "색상", type = "select", choices = ["블랙", "화이트"]
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardOptionItemResponse {

    // 옵션명 (예: 색상, 사이즈)
    private String name;

    // 옵션 타입 (select / text 등)
    private String type;

    // 필수 여부
    private Boolean required;

    // 선택지 (type == select 일 때만 사용)
    private List<String> choices;
}

