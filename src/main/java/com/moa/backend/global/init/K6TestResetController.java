package com.moa.backend.global.init;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * K6 부하 전후 DB 상태를 맞추기 위한 dev 전용 초기화 API.
 */
@RestController
@Profile("dev")
@RequiredArgsConstructor
public class K6TestResetController {

    private final K6TestResetService k6TestResetService;

    /** K6 테스트 프로젝트에 대해 loadtest 유저 주문을 삭제하고 리워드 재고를 시드 값으로 복구한다. */
    @PostMapping(value = "/public/k6-test-reset", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public String resetK6() {
        return k6TestResetService.resetK6LoadTestData();
    }
}
