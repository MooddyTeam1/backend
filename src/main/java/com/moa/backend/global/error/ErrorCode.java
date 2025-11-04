package com.moa.backend.global.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // 입력값 검증
    VALIDATION_FAILED("VALD-001", HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),

    // 인증 / 권한
    UNAUTHORIZED("AUTH-401", HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    FORBIDDEN("AUTH-403", HttpStatus.FORBIDDEN, "이 작업을 수행할 권한이 없습니다."),

    // 리소스 없음 / 충돌
    NOT_FOUND("COMM-404", HttpStatus.NOT_FOUND, "요청하신 대상을 찾을 수 없습니다."),
    BUSINESS_CONFLICT("BUSN-409", HttpStatus.CONFLICT, "요청이 현재 상태와 충돌합니다."),

    // 서버 내부 오류
    INTERNAL_ERROR("SYS-500", HttpStatus.INTERNAL_SERVER_ERROR, "서버에 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");

    private final String code;
    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(String code, HttpStatus status, String defaultMessage) {
        this.code = code;
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}

