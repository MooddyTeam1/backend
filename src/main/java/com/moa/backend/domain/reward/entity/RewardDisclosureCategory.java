package com.moa.backend.domain.reward.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 한글 설명: 전자상거래법상 리워드 정보고시에 사용하는 카테고리 Enum.
 * - 프론트/관리자 콘솔과 매핑해서 사용한다.
 */
@Getter
@RequiredArgsConstructor
public enum RewardDisclosureCategory {

    CLOTHING("의류"),
    FOOTWEAR("구두/신발"),
    BAG("가방"),
    FASHION_ACCESSORIES("패션잡화"),
    BEDDING("침구류/커튼"),
    FURNITURE("가구"),
    KITCHENWARE("주방용품"),
    COSMETICS("화장품"),
    JEWELRY("귀금속/보석/시계"),
    FOOD("식품"),
    HEALTH_FOOD("건강기능식품"),
    PROCESSED_FOOD("가공식품"),
    BABY_PRODUCTS("영유아용품"),
    BOOK("서적"),
    DIGITAL_CONTENT("디지털콘텐츠"),
    OTHER("기타");

    private final String labelKo; // 한글 라벨
}
