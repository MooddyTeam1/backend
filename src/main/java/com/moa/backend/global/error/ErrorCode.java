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

    ORDER_NOT_FOUND("ORD-404", HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    ORDER_ALREADY_PAID("ORD-409", HttpStatus.CONFLICT, "이미 결제 완료된 주문입니다."),
    ORDER_ALREADY_EXISTS("ORD-409B", HttpStatus.CONFLICT, "해당 프로젝트에 이미 주문이 존재합니다."),

    REWARD_NOT_FOUND("RWD-404", HttpStatus.NOT_FOUND, "선택한 리워드를 찾을 수 없습니다."),
    REWARD_OUT_OF_STOCK("RWD-409", HttpStatus.CONFLICT, "리워드 재고가 부족합니다."),

    PROJECT_NOT_FOUND("PRJ-404", HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다."),
    PROJECT_NOT_FUNDING("PRJ-409", HttpStatus.CONFLICT, "현재 펀딩 중이 아닌 프로젝트입니다."),

    PROJECT_ALREADY_DELETED("POST-410", HttpStatus.GONE, "이미 삭제된 게시글입니다."),
    PROJECT_DUPLICATE_TITLE("POST-BUSN-409", HttpStatus.CONFLICT, "동일한 제목의 게시글이 이미 존재합니다."),
    PROJECT_CANNOT_DELETE_IN_PROGRESS("POST-BUSN-410", HttpStatus.CONFLICT, "진행 중인 펀딩은 삭제할 수 없습니다."),

    PROJECT_ALREADY_FUNDING("PROJECT-409", HttpStatus.CONFLICT, "이미 승인되어 펀딩 중인 프로젝트입니다."),
    PROJECT_ALREADY_SUCCESS("PROJECT-409", HttpStatus.CONFLICT, "이미 펀딩이 완료된 프로젝트입니다."),
    PROJECT_ALREADY_ENDED("PROJECT-409", HttpStatus.CONFLICT, "반려되거나 종료된 프로젝트입니다."),

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

