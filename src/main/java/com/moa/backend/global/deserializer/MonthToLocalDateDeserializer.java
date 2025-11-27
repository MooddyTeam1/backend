package com.moa.backend.global.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 한글 설명: 프론트엔드에서 "YYYY-MM" 형식(예: "2025-12")으로 보내는 문자열을
 * LocalDate(해당 월의 첫째 날, 예: "2025-12-01")로 변환하는 커스텀 deserializer.
 * - 빈 문자열이나 null은 null로 반환
 * - "YYYY-MM-DD" 형식도 지원
 */
public class MonthToLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    // 한글 설명: "YYYY-MM-DD" 형식 파서 (예: "2025-12-01")
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        
        // 한글 설명: null이거나 빈 문자열이면 null 반환
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String trimmed = value.trim();
        
        try {
            // 한글 설명: "YYYY-MM" 형식인 경우 해당 월의 첫째 날로 변환
            if (trimmed.matches("^\\d{4}-\\d{2}$")) {
                LocalDate parsed = LocalDate.parse(trimmed + "-01", DATE_FORMATTER);
                return parsed;
            }
            // 한글 설명: "YYYY-MM-DD" 형식인 경우 그대로 파싱
            else if (trimmed.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                return LocalDate.parse(trimmed, DATE_FORMATTER);
            }
            // 한글 설명: 다른 형식은 기본 파서로 시도
            else {
                return LocalDate.parse(trimmed, DATE_FORMATTER);
            }
        } catch (DateTimeParseException e) {
            // 한글 설명: 파싱 실패 시 컨텍스트에 오류를 보고하고 null 반환
            throw new IOException(
                String.format("날짜 형식 오류: '%s'. 'YYYY-MM' 또는 'YYYY-MM-DD' 형식이 필요합니다.", value),
                e
            );
        }
    }
}

