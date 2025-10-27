package com.mooddy.backend.feature.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class OnboardingRequest {
    private List<String> genre;
    private List<String> artist;
}
