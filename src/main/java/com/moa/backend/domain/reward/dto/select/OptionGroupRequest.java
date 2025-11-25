package com.moa.backend.domain.reward.dto.select;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "리워드 옵션 그룹 요청")
public class OptionGroupRequest {
    @Schema(description = "옵션 그룹명", example = "색상")
    private String groupName;

    @Schema(description = "옵션 값 목록")
    private List<OptionValueRequest> optionValues;
}
