package com.moa.backend.domain.maker.controller;

import com.moa.backend.domain.maker.dto.publicpage.MakerDetailInfoResponse;
import com.moa.backend.domain.maker.dto.publicpage.MakerNewsPageResponse;
import com.moa.backend.domain.maker.dto.publicpage.MakerProjectsPageResponse;
import com.moa.backend.domain.maker.dto.publicpage.MakerPublicProfileResponse;
import com.moa.backend.domain.maker.service.MakerPublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 한글 설명: 메이커 공개 프로필/프로젝트/소식/상세 정보 조회 전용 컨트롤러.
 * - 인증 여부와 상관없이 메이커 페이지에서 필요한 공개 정보를 제공한다.
 * - 팔로우/언팔로우 액션은 /api/supporter-follows/makers/{makerId} 엔드포인트를 사용한다.
 */
@RestController
@RequestMapping("/public/makers")
@RequiredArgsConstructor
@Tag(name = "Maker-Public", description = "메이커 공개 프로필/프로젝트/소식 조회")
public class MakerPublicController {

    // 한글 설명: 메이커 공개 정보(프로필/프로젝트/소식/상세)를 제공하는 서비스 빈.
    private final MakerPublicService makerPublicService;

    /**
     * 한글 설명: 메이커 공개 프로필 조회 API.
     * - 메이커 이름, 이미지, 소개, 핵심 역량, 통계, isOwner/isFollowing 등을 포함한다.
     * - GET /public/makers/{makerId}
     */
    @GetMapping("/{makerId}")
    @Operation(summary = "메이커 공개 프로필 조회")
    public ResponseEntity<MakerPublicProfileResponse> getMakerPublicProfile(
            @Parameter(example = "1003") @PathVariable Long makerId
    ) {
        return ResponseEntity.ok(makerPublicService.getMakerPublicProfile(makerId));
    }

    /**
     * 한글 설명: 메이커의 프로젝트 목록 조회 API.
     * - 메이커가 진행한/진행 중인 프로젝트들을 페이지네이션하여 조회한다.
     * - status 파라미터로 LIVE/ENDED/SCHEDULED 등 상태 필터링을 지원할 예정이다.
     * - GET /public/makers/{makerId}/projects
     */
    @GetMapping("/{makerId}/projects")
    @Operation(summary = "메이커 프로젝트 목록 조회")
    public ResponseEntity<MakerProjectsPageResponse> getMakerProjects(
            @Parameter(example = "1003") @PathVariable Long makerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(
                makerPublicService.getMakerProjects(makerId, page, size, status)
        );
    }

    /**
     * 한글 설명: 메이커 소식(업데이트) 목록 조회 API.
     * - 메이커가 올린 공지/진행 현황/비하인드 등 소식을 페이지네이션하여 조회한다.
     * - GET /public/makers/{makerId}/news
     */
    @GetMapping("/{makerId}/news")
    public ResponseEntity<MakerNewsPageResponse> getMakerNews(
            @PathVariable Long makerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                makerPublicService.getMakerNews(makerId, page, size)
        );
    }

    /**
     * 한글 설명: 메이커 상세 정보 조회 API.
     * - 사업자 정보(업종, 업태, 사업자등록번호, 통신판매번호 등)와 연락처, 위치 정보를 반환한다.
     * - 프로젝트 상세 페이지 하단의 “메이커 정보” 섹션에 사용하기 좋다.
     * - GET /public/makers/{makerId}/info
     */
    @GetMapping("/{makerId}/info")
    @Operation(summary = "메이커 사업자 정보 조회")
    public ResponseEntity<MakerDetailInfoResponse> getMakerInfo(
            @Parameter(example = "1003") @PathVariable Long makerId
    ) {
        return ResponseEntity.ok(makerPublicService.getMakerDetailInfo(makerId));
    }
}
