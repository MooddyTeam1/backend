package com.moa.backend.domain.maker.dto.publicpage;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 한글 설명: 메이커 키워드 DTO.
 * - 현재는 Maker 엔티티의 keywords 문자열을 파싱해 name만 채워서 사용.
 * - 추후 maker_keywords 테이블 도입 시 id 필드를 실제 PK로 매핑 가능.
 */
@Schema(description = "메이커 키워드 DTO")
public record MakerKeywordDTO(
        @Schema(description = "키워드 ID", example = "1")
        Long id,      // 한글 설명: 키워드 ID
        @Schema(description = "키워드명", example = "친환경")
        String name   // 한글 설명: 키워드 이름 (예: "친환경", "소셜임팩트")
) {
}
