package com.moa.backend.global.scheduler;

import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 배치 작업 공통 로깅/메트릭/예외 처리를 담당한다.
 * Step 0에서 정의한 가이드에 맞춰 모든 스케줄러가 동일한 패턴으로 실행되도록 강제한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerSupport {

    private final MeterRegistry meterRegistry;

    /**
     * 스케줄러 작업을 안전하게 실행하고, 성공/실패 메트릭을 남긴다.
     * 예외는 다시 던져 트랜잭션 롤백 및 재시도를 유도한다.
     */
    public void runSafely(String jobName, Callable<Void> task) {
        long start = System.currentTimeMillis();
        try {
            log.info("[{}] batch start", jobName);
            task.call();
            long took = System.currentTimeMillis() - start;
            log.info("[{}] batch success ({} ms)", jobName, took);
            meterRegistry.counter("batch.success", "job", jobName).increment();
        } catch (Exception ex) {
            log.error("[{}] batch failed", jobName, ex);
            meterRegistry.counter("batch.failure", "job", jobName).increment();
            if (ex instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new IllegalStateException("Scheduler task failed: " + jobName, ex);
        }
    }
}
