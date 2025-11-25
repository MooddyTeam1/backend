package com.moa.backend.domain.makernews.service;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.makernews.dto.MakerNewsCreateRequest;
import com.moa.backend.domain.makernews.dto.MakerNewsResponse;
import com.moa.backend.domain.makernews.dto.MakerNewsUpdateRequest;
import com.moa.backend.domain.makernews.entity.MakerNews;
import com.moa.backend.domain.makernews.entity.MakerNewsType;
import com.moa.backend.domain.makernews.repository.MakerNewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 한글 설명: 메이커 소식 생성/수정/삭제/조회 비즈니스 로직.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MakerNewsService {

    private final MakerRepository makerRepository;
    private final MakerNewsRepository makerNewsRepository;

    /**
     * 한글 설명: 현재 로그인한 유저 기준으로 메이커 소식을 생성한다.
     */
    public MakerNewsResponse createNews(Long currentUserId, MakerNewsCreateRequest request) {
        // 한글 설명: 유저가 가진 메이커 조회
        Maker maker = makerRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("메이커 정보를 찾을 수 없습니다. (userId=" + currentUserId + ")"));

        MakerNews news = MakerNews.create(
                maker,
                request.getTitle(),
                request.getContentMarkdown(),
                request.getNewsType()
        );

        MakerNews saved = makerNewsRepository.save(news);
        return MakerNewsResponse.from(saved);
    }

    /**
     * 한글 설명: 특정 메이커의 소식 목록(공개용)을 최신순으로 조회한다.
     * - page: 1부터 시작
     * - pageSize: 기본 20, 최대 100
     */
    @Transactional(readOnly = true)
    public List<MakerNewsResponse> getMakerNewsList(Long makerId,
                                                    int page,
                                                    int pageSize,
                                                    MakerNewsType newsType) {

        int safePage = Math.max(page, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 100);
        Pageable pageable = PageRequest.of(safePage - 1, safePageSize);

        if (newsType != null) {
            return makerNewsRepository
                    .findByMakerIdAndNewsTypeOrderByCreatedAtDesc(makerId, newsType, pageable)
                    .stream()
                    .map(MakerNewsResponse::from)
                    .toList();
        } else {
            return makerNewsRepository
                    .findByMakerIdOrderByCreatedAtDesc(makerId, pageable)
                    .stream()
                    .map(MakerNewsResponse::from)
                    .toList();
        }
    }

    /**
     * 한글 설명: (소유자 기준) 특정 소식을 단건 조회한다.
     * - 명세상 현재는 소유자만 조회 가능.
     */
    @Transactional(readOnly = true)
    public MakerNewsResponse getMyNewsDetail(Long currentUserId, Long newsId) {
        Maker maker = makerRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("메이커 정보를 찾을 수 없습니다. (userId=" + currentUserId + ")"));

        MakerNews news = makerNewsRepository.findByIdAndMakerId(newsId, maker.getId())
                .orElseThrow(() -> new IllegalArgumentException("소식을 찾을 수 없습니다. (newsId=" + newsId + ")"));

        return MakerNewsResponse.from(news);
    }

    /**
     * 한글 설명: 현재 로그인한 메이커의 소식을 수정한다.
     */
    public MakerNewsResponse updateMyNews(Long currentUserId,
                                          Long newsId,
                                          MakerNewsUpdateRequest request) {
        Maker maker = makerRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("메이커 정보를 찾을 수 없습니다. (userId=" + currentUserId + ")"));

        MakerNews news = makerNewsRepository.findByIdAndMakerId(newsId, maker.getId())
                .orElseThrow(() -> new IllegalArgumentException("소식을 찾을 수 없습니다. (newsId=" + newsId + ")"));

        news.update(
                request.getTitle(),
                request.getContentMarkdown(),
                request.getNewsType()
        );

        return MakerNewsResponse.from(news);
    }

    /**
     * 한글 설명: 현재 로그인한 메이커의 소식을 삭제한다.
     * - 지금은 하드 삭제. 필요 시 소프트 삭제 플래그로 확장 가능.
     */
    public void deleteMyNews(Long currentUserId, Long newsId) {
        Maker maker = makerRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("메이커 정보를 찾을 수 없습니다. (userId=" + currentUserId + ")"));

        MakerNews news = makerNewsRepository.findByIdAndMakerId(newsId, maker.getId())
                .orElseThrow(() -> new IllegalArgumentException("소식을 찾을 수 없습니다. (newsId=" + newsId + ")"));

        makerNewsRepository.delete(news);
    }
}
