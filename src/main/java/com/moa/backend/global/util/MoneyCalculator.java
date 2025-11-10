package com.moa.backend.global.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 금액 계산을 한곳에서 처리해 항상 내림(RoundingMode.DOWN) 규칙을 적용하도록 보장하는 유틸 클래스이다.
 */
public final class MoneyCalculator {

    private MoneyCalculator() {
        // Utility class
    }

    /**
     * 기준 금액의 일정 비율을 계산한다. 항상 {@link RoundingMode#DOWN}으로 내림 처리된다.
     *
     * @param amount 기준 금액
     * @param rate   비율 (예: 0.05 = 5%)
     * @return 내림 처리된 결과 금액
     */
    public static long percentageOf(long amount, double rate) {
        return BigDecimal.valueOf(amount)
                .multiply(BigDecimal.valueOf(rate))
                .setScale(0, RoundingMode.DOWN)
                .longValueExact();
    }

    /**
     * 기준 금액에 delta를 더한다. 항상 {@link RoundingMode#DOWN}으로 내림 처리된다.
     * 장부에 돈을 더할 때는 반드시 이 메서드를 사용해 소수점이 남지 않도록 맞춘다.
     *
     * @param base  기준 금액
     * @param delta 더할 금액
     * @return base + delta 내림 결과
     */
    public static long add(long base, long delta) {
        return BigDecimal.valueOf(base)
                .add(BigDecimal.valueOf(delta))
                .setScale(0, RoundingMode.DOWN)
                .longValueExact();
    }

    /**
     * 기준 금액에서 delta를 뺀다. 항상 {@link RoundingMode#DOWN}으로 내림 처리된다.
     * 장부에 돈을 뺄 때는 반드시 이 메서드를 사용해 소수점이 남지 않도록 맞춘다.
     *
     * @param base  기준 금액
     * @param delta 뺄 금액
     * @return base - delta 내림 결과
     */
    public static long subtract(long base, long delta) {
        return BigDecimal.valueOf(base)
                .subtract(BigDecimal.valueOf(delta))
                .setScale(0, RoundingMode.DOWN)
                .longValueExact();
    }
}
