package com.moa.backend.domain.reward.dto.select;

import com.moa.backend.domain.reward.entity.OptionGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "리워드 옵션 그룹 응답")
public class OptionGroupResponse {
    @Schema(description = "옵션 그룹명", example = "색상")
    private String groupName;

    @Schema(description = "옵션 값 목록")
    private List<OptionValueResponse> optionValues;

    public static OptionGroupResponse from(OptionGroup optionGroup) {
        return OptionGroupResponse.builder()
                .groupName(optionGroup.getGroupName())
                .optionValues(optionGroup.getOptionValues() != null ?
                        optionGroup.getOptionValues().stream()
                                .map(OptionValueResponse::from).toList() : null)
                .build();
    }
}
