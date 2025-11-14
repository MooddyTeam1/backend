package com.moa.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 공통 스케줄러 스레드 풀 설정.
 * 여러 배치 작업이 동시에 돌아도 지연되지 않도록 별도 풀을 제공한다.
 */
@Configuration
public class SchedulerConfig {

    /**
     * Step 0 요구사항: 스케줄러 전용 스레드 풀을 명시적으로 구성한다.
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(4); // 선/후지급, 배송, 잔금 배치를 동시에 돌릴 수 있도록 4개 스레드 사용
        scheduler.setThreadNamePrefix("moa-scheduler-");
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.initialize(); // 명시적으로 초기화해 부트스트랩 시점에 풀을 준비한다.
        return scheduler;
    }
}
