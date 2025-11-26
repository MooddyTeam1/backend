package com.moa.backend.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 한글 설명: 리워드 옵션 구성 DTO.
 * - 예: 색상/사이즈 같은 옵션을 어떻게 노출할지에 대한 설정.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardOptionConfigResponse {

    // 한글 설명: 옵션 존재 여부 (false 이면 옵션 없음)
    private Boolean hasOptions;

    // 한글 설명: 옵션 리스트
    private List<RewardOptionItemResponse> options;

    /**
     * 한글 설명: optionConfigJson 을 파싱해서 생성하는 정적 메서드는
     * 추후 ObjectMapper 를 주입해서 구현하는 것을 추천.
     */
}

