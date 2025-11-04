package com.moa.backend.global.error;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorResponse {

    private final String timestamp;
    private final int status;
    private final String code;
    private final String message;

    public ErrorResponse(int status, String code, String message) {
        this.timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        this.status = status;
        this.code = code;
        this.message = message;
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
}

