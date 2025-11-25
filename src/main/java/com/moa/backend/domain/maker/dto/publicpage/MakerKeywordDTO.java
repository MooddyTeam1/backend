package com.moa.backend.domain.maker.dto.publicpage;

/**
 * 한글 설명: 메이커 키워드 DTO.
 * - 현재는 Maker 엔티티의 keywords 문자열을 파싱해 name만 채워서 사용.
 * - 추후 maker_keywords 테이블 도입 시 id 필드를 실제 PK로 매핑 가능.
 */
public record MakerKeywordDTO(
        Long id,      // 한글 설명: 키워드 ID
        String name   // 한글 설명: 키워드 이름 (예: "친환경", "소셜임팩트")
) {
}
