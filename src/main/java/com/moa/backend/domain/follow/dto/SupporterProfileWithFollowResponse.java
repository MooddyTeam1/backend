// src/main/java/com/moa/backend/domain/user/dto/SupporterProfileWithFollowResponse.java
package com.moa.backend.domain.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class SupporterProfileWithFollowResponse {

    private Long userId;
    private String displayName;
    private String bio;
    private String imageUrl;
    private String phone;
    private String address1;
    private String address2;
    private String postalCode;
    private String interests;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private long followingSupporterCount;
    private long followingMakerCount;
    private List<SimpleSupporterSummary> followingSupporters;
    private List<SimpleMakerSummary> followingMakers;
}
