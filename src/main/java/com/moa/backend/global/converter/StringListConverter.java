package com.moa.backend.global.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    // 자바 -> DB
    @Override
    public String convertToDatabaseColumn(List<String> list) {
        try {
            return mapper.writeValueAsString(list); // ["a","b","c"]
        } catch (JsonProcessingException e) {
            throw new RuntimeException("리스트 → JSON 변환 오류", e);
        }
    }

    // DB -> 자바
    @Override
    public List<String> convertToEntityAttribute(String json) {
        try {
            if (json == null || json.isBlank()) return new ArrayList<>();
            return mapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("JSON → 리스트 변환 오류", e);
        }
    }
}
