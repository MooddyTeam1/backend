package com.mooddy.backend.external.itunes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItunesResponse {
    private int resultCount;
    private List<Map<String, Object>> results;
}
