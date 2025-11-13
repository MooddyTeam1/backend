package com.moa.backend.domain.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RejectProjectRequest {
    private String reason;
}
