package com.moa.backend.global.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final String timestamp;
    private final int status;
    private final String code;
    private final String message;
    /** dev 등에서만 설정: 미처리 예외 원인 분류(JDBC/스레드 등) */
    private final String diagnostic;

    public ErrorResponse(int status, String code, String message) {
        this(status, code, message, null);
    }

    public ErrorResponse(int status, String code, String message, String diagnostic) {
        this.timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        this.status = status;
        this.code = code;
        this.message = message;
        this.diagnostic = diagnostic;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDiagnostic() {
        return diagnostic;
    }
}

