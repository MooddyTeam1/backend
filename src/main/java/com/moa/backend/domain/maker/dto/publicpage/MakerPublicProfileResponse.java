package com.moa.backend.domain.maker.dto.publicpage;

import java.util.List;

/**
 * 한글 설명: 메이커 공개 프로필 조회 응답 DTO.
 * - /public/makers/{makerId} 에서 사용한다.
 */
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메이커 공개 프로필 응답")
public record MakerPublicProfileResponse(
        @Schema(description = "메이커 ID", example = "1000")
        String makerId,                     // 한글 설명: 메이커 ID ("1000")
        @Schema(description = "메이커 소유자 유저 ID", example = "1")
        String ownerUserId,                 // 한글 설명: 메이커 소유자 유저 ID
        @Schema(description = "메이커 이름", example = "모아 스튜디오")
        String name,                        // 한글 설명: 메이커 이름
        @Schema(description = "커버/브랜드 이미지 URL", example = "https://cdn.moa.com/maker/cover.png")
        String imageUrl,                    // 한글 설명: 커버/브랜드 이미지 URL (nullable)
        @Schema(description = "제품/서비스 소개", example = "친환경 텀블러 제조")
        String productIntro,                // 한글 설명: 제품/서비스 소개 (nullable)
        @Schema(description = "핵심 역량", example = "제조/디자인/브랜딩")
        String coreCompetencies,            // 한글 설명: 핵심 역량 (nullable)
        @Schema(description = "키워드 ID 목록", example = "[1,2,3]")
        List<Long> keywordIds,              // 한글 설명: 키워드 ID 목록 (현재는 null/빈 리스트 사용 가능)
        @Schema(description = "키워드 상세 정보 목록")
        List<MakerKeywordDTO> keywords,     // 한글 설명: 키워드 상세 정보 목록
        @Schema(description = "누적 모금액", example = "10000000")
        Long totalRaised,                   // 한글 설명: 누적 모금액
        @Schema(description = "누적 서포터 수", example = "1234")
        Integer totalSupporters,            // 한글 설명: 누적 서포터 수
        @Schema(description = "만족도 평점", example = "4.8")
        Double satisfactionRate,            // 한글 설명: 만족도 평점 (리뷰 없으면 null)
        @Schema(description = "현재 유저 기준 팔로우 여부", example = "true")
        Boolean isFollowing,                // 한글 설명: 현재 로그인 유저 기준 팔로우 여부 (비로그인 시 null)
        @Schema(description = "현재 유저가 소유자인지 여부", example = "false")
        Boolean isOwner                     // 한글 설명: 현재 로그인 유저가 이 메이커의 소유자인지 여부
) {
}
