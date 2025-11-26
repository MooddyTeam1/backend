// 한글 설명: 평소 후원 예산 범위
package com.moa.backend.domain.onboarding.model;

/**
 * 한글 설명: 평소 후원 예산 범위
 *  - FRONT에서 라벨만 한글로 보여주고, 서버에는 이 enum 이름을 보낸다고 가정
 */
public enum BudgetRange {
    UNDER_50K,         // 5만원 미만
    BETWEEN_50K_100K,  // 5만 ~ 10만
    BETWEEN_100K_200K, // 10만 ~ 20만
    BETWEEN_200K_500K, // 20만 ~ 50만
    ABOVE_500K,        // 50만 이상
    NO_PREFERENCE      // 선호 없음
}