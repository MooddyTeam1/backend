// 한글 설명: 프로젝트 Q&A 도메인 서비스 인터페이스
package com.moa.backend.domain.qna.service;

import com.moa.backend.domain.maker.dto.manageproject.ProjectQnaResponse;
import com.moa.backend.domain.qna.dto.ProjectQnaAnswerRequest;
import com.moa.backend.domain.qna.dto.ProjectQnaCreateRequest;
import com.moa.backend.global.dto.PageResponse;

import java.util.List;

public interface ProjectQnaService {

    // 서포터: 질문 생성
    ProjectQnaResponse createQuestion(Long supporterUserId, Long projectId, ProjectQnaCreateRequest request);

    // 서포터: 내가 남긴 Q&A 목록 조회
    List<ProjectQnaResponse> getMyQnaList(Long supporterUserId, Long projectId);

    // 서포터: 내가 남긴 Q&A 단건 조회
    ProjectQnaResponse getMyQna(Long supporterUserId, Long projectId, Long qnaId);

    // 메이커: Q&A 답변 등록/수정
    ProjectQnaResponse answerQuestion(Long makerUserId, Long projectId, Long qnaId, ProjectQnaAnswerRequest request);

    // (기존) 메이커: Q&A 목록 조회 (요약/기타 용도)
    List<ProjectQnaResponse> getQnaListForMaker(Long makerUserId, Long projectId, boolean unansweredOnly);

    // 🔥 (신규) 메이커: Q&A 페이지네이션 + 미답변 필터
    PageResponse<ProjectQnaResponse> getQnaPageForMaker(
            Long makerUserId,
            Long projectId,
            boolean unansweredOnly,
            int page,
            int size
    );
}
