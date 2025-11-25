package com.moa.backend.domain.news.service;

import com.moa.backend.domain.news.dto.NewsCreateRequest;
import com.moa.backend.domain.news.dto.NewsResponse;

import java.util.List;

public interface ProjectNewsService {
    NewsResponse createNews(Long creatorId, Long projectId, NewsCreateRequest request);
    List<NewsResponse> getNewsList(Long projectId);
    NewsResponse getNews(Long newsId);
    void deleteNews(Long creatorId, Long newsId);
}
