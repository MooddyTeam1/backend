package com.moa.backend.global.init;

/**
 * {@link TestDataInitController} 와 K6 부하/초기화 API가 공유하는 상수.
 */
public final class K6LoadTestConstants {

    /** test-init 으로 생성되는 K6 전용 프로젝트 제목 */
    public static final String PROJECT_TITLE = "오픈런 스마트 워치 2세대 — K6 선착순 부하 테스트";

    /** test-init 시 시드 리워드의 초기 재고 (리셋 시에도 동일 값으로 복구) */
    public static final int REWARD_INITIAL_STOCK = 100;

    private K6LoadTestConstants() {}
}
