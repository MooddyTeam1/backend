package com.moa.backend.domain.maker.service;

import com.moa.backend.domain.maker.dto.ProjectNoticeCreateRequest;
import com.moa.backend.domain.maker.dto.manageproject.ProjectNoticeResponse;
import com.moa.backend.global.dto.PageResponse;
import java.time.LocalDate;
import java.util.List;

/**
 * 한글 설명: 프로젝트 소식(새소식) 도메인 서비스 인터페이스.
 * - 메이커 전용 작성/수정/삭제
 * - 공개용 목록/단건 조회
 */
public interface ProjectNewsService {

    // 메이커: 소식 생성
    ProjectNoticeResponse createNews(Long creatorId, Long projectId, ProjectNoticeCreateRequest request);

    // 공개: 특정 프로젝트의 소식 목록
    List<ProjectNoticeResponse> getNewsList(Long projectId);

    // 공개: 특정 프로젝트의 소식 단건 조회
    ProjectNoticeResponse getNews(Long projectId, Long newsId);

    // 메이커: 소식 수정
    ProjectNoticeResponse updateNews(Long creatorId, Long projectId, Long newsId, ProjectNoticeCreateRequest request);

    // 메이커: 소식 삭제
    void deleteNews(Long creatorId, Long projectId, Long newsId);

    // =============================
    // 메이커 콘솔: 페이지네이션 목록
    // =============================
    /**
     * 한글 설명:
     *  - 메이커 콘솔에서 사용하는 프로젝트 소식 목록 조회
     *  - pinned DESC, createdAt DESC 정렬
     *  - 검색(제목/내용), 기간 필터(from~to) 지원
     */
    PageResponse<ProjectNoticeResponse> getNewsPageForMaker(
            Long makerUserId,
            Long projectId,
            int page,
            int size,
            String keyword,
            LocalDate from,
            LocalDate to
    );
}
