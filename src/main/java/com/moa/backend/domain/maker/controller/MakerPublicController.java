package com.moa.backend.domain.maker.controller;

import com.moa.backend.domain.maker.dto.MakerProjectPageResponse;
import com.moa.backend.domain.maker.dto.MakerPublicProfileResponse;
import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.maker.service.MakerProjectQueryService;
import com.moa.backend.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 한글 설명: 메이커 공개 정보(프로필, 프로젝트 등)를 제공하는 컨트롤러.
 * - URL prefix: /public/makers
 * - 인증 필요 없음 (공개용 API)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/public/makers")
@Tag(name = "Maker-Public", description = "공개 메이커 정보 조회")
public class MakerPublicController {

    // 한글 설명: 메이커 기본 정보 조회용
    private final MakerRepository makerRepository;

    // 한글 설명: 메이커 홈(프로필 페이지)에서 사용하는 프로젝트 목록 조회 서비스.
    private final MakerProjectQueryService makerProjectQueryService;

    // =====================================================================
    // 0) 메이커 공개 프로필 조회
    //    GET /public/makers/{makerId}
    //
    // - 프론트: makerService.getPublicProfile(makerId)가 여기를 호출 중
    // =====================================================================
    @GetMapping("/{makerId}")
    @Operation(summary = "메이커 공개 프로필 조회", description = "공개 메이커 프로필 정보를 조회합니다. 비공개 내부 정보는 반환되지 않습니다.")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
            @ApiResponse(responseCode = "404", description = "메이커를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MakerPublicProfileResponse> getMakerPublicProfile(
            @Parameter(description = "조회할 메이커 ID", example = "310") @PathVariable("makerId") Long makerId
    ) {
        /*
         * 한글 설명:
         * - 메이커 공개 프로필 기본 정보만 반환.
         * - 추가 확장: 이후에 projects, news 등 섹션을 함께 넣고 싶으면
         *   MakerPublicProfileResponse에 필드를 추가해서 확장 가능.
         */

        Maker maker = makerRepository.findById(makerId)
                .orElseThrow(() -> new IllegalArgumentException("메이커를 찾을 수 없습니다. id=" + makerId));

        MakerPublicProfileResponse response = MakerPublicProfileResponse.from(maker);

        return ResponseEntity.ok(response);
    }

    // =====================================================================
    // 1) 메이커 프로젝트 목록 조회
    //    GET /public/makers/{makerId}/projects
    //
    // - 프론트 명세:
    //   GET /api/makers/{makerId}/projects?page=1&size=12&sort=createdAt&order=desc
    //   → 백엔드에서는 /public/makers 로 프리픽스 통일
    // =====================================================================
    @GetMapping("/{makerId}/projects")
    @Operation(summary = "메이커 공개 프로젝트 목록 조회", description = "메이커 공개 페이지의 프로젝트 탭 목록을 페이징 조회합니다. page는 1부터 시작하며 size는 과도한 값이 제한됩니다.")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로젝트 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "페이지/정렬 파라미터가 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "메이커를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MakerProjectPageResponse> getMakerProjects(
            @Parameter(description = "조회할 메이커 ID", example = "310") @PathVariable("makerId") Long makerId,
            @Parameter(description = "페이지 번호(1부터 시작)", example = "1") @RequestParam(name = "page", defaultValue = "1") int page,
            @Parameter(description = "페이지 크기(기본 12, 최대 50)", example = "12") @RequestParam(name = "size", defaultValue = "12") int size,
            @Parameter(description = "정렬 기준(createdAt/startDate/endDate/raisedAmount)", example = "createdAt") @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
            @Parameter(description = "정렬 방향(asc/desc)", example = "desc") @RequestParam(name = "order", defaultValue = "desc") String order
    ) {
        /*
         * 한글 설명:
         * - 메이커 프로필 페이지 "프로젝트" 탭에서 사용하는 목록 데이터를 조회한다.
         * - 포함되는 프로젝트:
         *   * SCHEDULED (공개 예정)
         *   * LIVE (진행 중)
         *   * ENDED + SUCCESS (성공 종료)
         * - page: 1부터 시작 (서비스 내부에서 0-based로 변환)
         * - size: 기본 12, 최대 50 (서비스에서 검증)
         * - sort: createdAt / startDate / endDate / raisedAmount
         * - order: asc / desc
         */

        MakerProjectPageResponse response = makerProjectQueryService.getMakerProjects(
                makerId,
                page,
                size,
                sort,
                order
        );

        return ResponseEntity.ok(response);
    }

    // 🔥 주의:
    // - /public/makers/{makerId} 와 /public/makers/{makerId}/projects 를
    //   각각 하나씩만 유지해야 함.
    //   같은 URL 패턴에 대해 메서드를 두 개 이상 만들면 Ambiguous mapping 에러 발생.
}
