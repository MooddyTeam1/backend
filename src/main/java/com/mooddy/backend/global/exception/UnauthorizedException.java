package com.mooddy.backend.global.exception;

/**
 * 권한이 없을 때 발생하는 예외
 * HTTP 403 Forbidden 상태 코드와 매핑됨
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
