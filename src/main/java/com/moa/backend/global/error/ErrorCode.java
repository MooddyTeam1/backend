package com.moa.backend.global.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // 입력값 검증
    VALIDATION_FAILED("VALD-001", HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    INVALID_AMOUNT("POST-VALD-002", HttpStatus.BAD_REQUEST, "목표 금액은 0보다 커야 합니다."),
    INVALID_DATE("POST-VALD-003", HttpStatus.BAD_REQUEST, "펀딩 시작일과 종료일이 올바르지 않습니다."),

    // 인증 / 권한
    UNAUTHORIZED("AUTH-401", HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    FORBIDDEN("AUTH-403", HttpStatus.FORBIDDEN, "이 작업을 수행할 권한이 없습니다."),
    PROJECT_FORBIDDEN("POST-AUTH-403", HttpStatus.FORBIDDEN, "게시글 수정 또는 삭제 권한이 없습니다."),

    // 리소스 없음 / 충돌
    NOT_FOUND("COMM-404", HttpStatus.NOT_FOUND, "요청하신 대상을 찾을 수 없습니다."),
    BUSINESS_CONFLICT("BUSN-409", HttpStatus.CONFLICT, "요청이 현재 상태와 충돌합니다."),

    PROJECT_NOT_FOUND("POST-404", HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    PROJECT_ALREADY_DELETED("POST-410", HttpStatus.GONE, "이미 삭제된 게시글입니다."),
    PROJECT_DUPLICATE_TITLE("POST-BUSN-409", HttpStatus.CONFLICT, "동일한 제목의 게시글이 이미 존재합니다."),
    PROJECT_CANNOT_DELETE_IN_PROGRESS("POST-BUSN-410", HttpStatus.CONFLICT, "진행 중인 펀딩은 삭제할 수 없습니다."),

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

