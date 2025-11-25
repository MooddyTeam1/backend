package com.moa.backend.domain.makernews.controller;

import com.moa.backend.domain.makernews.dto.MakerNewsCreateRequest;
import com.moa.backend.domain.makernews.dto.MakerNewsResponse;
import com.moa.backend.domain.makernews.dto.MakerNewsUpdateRequest;
import com.moa.backend.domain.makernews.entity.MakerNewsType;
import com.moa.backend.domain.makernews.service.MakerNewsService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal; // ✅ JwtUserPrincipal import
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 한글 설명: 메이커 소식 API 컨트롤러.
 * - 0번 이미지 업로드 API는 별도 Upload 컨트롤러에 구현되어 있다고 가정.
 *
 * 엔드포인트 목록:
 * - POST   /api/maker/news            : 메이커 소식 생성
 * - GET    /api/makers/{makerId}/news : 특정 메이커 소식 목록 조회(공개)
 * - GET    /api/maker/news/{newsId}   : 소식 상세 조회(소유자 전용)
 * - PUT    /api/maker/news/{newsId}   : 소식 수정(소유자 전용)
 * - DELETE /api/maker/news/{newsId}   : 소식 삭제(소유자 전용)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MakerNewsController {

    private final MakerNewsService makerNewsService;

    /**
     * 한글 설명: 메이커 소식 생성 API.
     * - POST /api/maker/news
     * - 인증 필요, 현재 로그인한 유저의 메이커에 소식을 등록한다.
     */
    @PostMapping("/maker/news")
    public ResponseEntity<MakerNewsResponse> createNews(
            // 한글 설명: JWT 인증 정보에서 유저 ID를 꺼내오기 위해 JwtUserPrincipal 사용
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestBody @Valid MakerNewsCreateRequest request
    ) {
        Long currentUserId = principal.getId(); // 한글 설명: 현재 로그인한 유저 ID
        MakerNewsResponse response = makerNewsService.createNews(currentUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 한글 설명: 특정 메이커의 소식 목록 조회(공개 API).
     * - GET /api/makers/{makerId}/news
     * - page: 1부터 시작하는 페이지 번호 (기본값 1)
     * - pageSize: 페이지 크기 (기본값 20, 최대 100)
     * - newsType: EVENT | NOTICE | NEW_PRODUCT (선택, 필터용)
     */
    @GetMapping("/makers/{makerId}/news")
    public ResponseEntity<List<MakerNewsResponse>> getMakerNewsList(
            @PathVariable Long makerId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "20") int pageSize,
            @RequestParam(name = "newsType", required = false) MakerNewsType newsType
    ) {
        List<MakerNewsResponse> responses =
                makerNewsService.getMakerNewsList(makerId, page, pageSize, newsType);
        return ResponseEntity.ok(responses);
    }

    /**
     * 한글 설명: 메이커 소식 상세 조회(소유자 전용).
     * - GET /api/maker/news/{newsId}
     * - 현재 로그인한 메이커의 소식이 아닐 경우 403 처리(글로벌 예외 핸들러에서 구현).
     */
    @GetMapping("/maker/news/{newsId}")
    public ResponseEntity<MakerNewsResponse> getMyNewsDetail(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long newsId
    ) {
        Long currentUserId = principal.getId();
        MakerNewsResponse response = makerNewsService.getMyNewsDetail(currentUserId, newsId);
        return ResponseEntity.ok(response);
    }

    /**
     * 한글 설명: 메이커 소식 수정 API.
     * - PUT /api/maker/news/{newsId}
     */
    @PutMapping("/maker/news/{newsId}")
    public ResponseEntity<MakerNewsResponse> updateMyNews(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long newsId,
            @RequestBody @Valid MakerNewsUpdateRequest request
    ) {
        Long currentUserId = principal.getId();
        MakerNewsResponse response = makerNewsService.updateMyNews(currentUserId, newsId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 한글 설명: 메이커 소식 삭제 API.
     * - DELETE /api/maker/news/{newsId}
     */
    @DeleteMapping("/maker/news/{newsId}")
    public ResponseEntity<Void> deleteMyNews(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long newsId
    ) {
        Long currentUserId = principal.getId();
        makerNewsService.deleteMyNews(currentUserId, newsId);
        return ResponseEntity.noContent().build();
    }
}
