package com.moa.backend.domain.project.news.service;

import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.project.news.dto.NewsCreateRequest;
import com.moa.backend.domain.project.news.dto.NewsResponse;
import com.moa.backend.domain.project.news.entity.ProjectNews;
import com.moa.backend.domain.project.news.entity.ProjectNewsImage;
import com.moa.backend.domain.project.news.repository.ProjectNewsRepository;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectNewsServiceImpl implements ProjectNewsService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectNewsRepository projectNewsRepository;

    @Override
    @Transactional
    public NewsResponse createNews(Long creatorId, Long projectId, NewsCreateRequest request) {

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        // maker 권한 체크
        if (!project.getMaker().getOwner().getId().equals(creatorId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        ProjectNews news = ProjectNews.builder()
                .project(project)
                .creator(creator)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        // 이미지 처리
        if (request.getImageUrls() != null) {
            request.getImageUrls().forEach(url -> {
                news.addImage(
                        ProjectNewsImage.builder().imageUrl(url).build()
                );
            });
        }

        ProjectNews saved = projectNewsRepository.save(news);
        return NewsResponse.from(saved);
    }

    @Override
    public List<NewsResponse> getNewsList(Long projectId) {
        return projectNewsRepository.findByProject_IdOrderByPinnedDescCreatedAtDesc(projectId)
                .stream()
                .map(NewsResponse::from)
                .toList();
    }

    @Override
    public NewsResponse getNews(Long newsId) {
        ProjectNews news = projectNewsRepository.findById(newsId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        return NewsResponse.from(news);
    }

    @Override
    @Transactional
    public void deleteNews(Long creatorId, Long newsId) {

        ProjectNews news = projectNewsRepository.findById(newsId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (!news.getCreator().getId().equals(creatorId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        projectNewsRepository.delete(news);
    }
}
