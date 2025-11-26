package com.moa.backend.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 한글 설명: 반려 사유 프리셋 목록 응답 DTO.
 * - 심사자가 자주 사용하는 문구를 프론트에서 선택할 수 있도록 지원.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RejectReasonPresetResponse {

    private List<String> presets;

    /**
     * 한글 설명: 기본 프리셋 목록을 반환하는 정적 메서드.
     * - 운영팀 합의 후 수정 가능.
     */
    public static RejectReasonPresetResponse defaultPresets() {
        return RejectReasonPresetResponse.builder()
                .presets(List.of(
                        "근거 자료 부족(증빙/계약서/허가서)",
                        "리워드/배송/환불 정책 미흡",
                        "금지 콘텐츠/정책 위반 가능성",
                        "상표권/저작권/초상권 우려",
                        "메이커 신원/연락처 불명확",
                        "위험물/규제 품목 포함 우려",
                        "광고성/과장 표현 과다"
                ))
                .build();
    }
}

