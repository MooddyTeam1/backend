// 한글 설명: 서포터 프로필 기준 프로젝트 찜(북마크) 비즈니스 로직 서비스.
package com.moa.backend.domain.follow.service;

import com.moa.backend.domain.follow.entity.SupporterBookmarkProject;
import com.moa.backend.domain.follow.repository.SupporterBookmarkProjectRepository;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.user.entity.SupporterProfile;
import com.moa.backend.domain.user.repository.SupporterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class SupporterProjectBookmarkService {

    private final SupporterBookmarkProjectRepository bookmarkRepository;
    private final SupporterProfileRepository supporterProfileRepository;
    private final ProjectRepository projectRepository;

    // 한글 설명: 로그인 유저 기준으로 프로젝트 찜 상태 + 전체 찜 개수를 조회한다.
    @Transactional(readOnly = true)
    public BookmarkStatus getStatus(Long userId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("프로젝트를 찾을 수 없습니다."));

        long totalCount = bookmarkRepository.countByProject(project);

        // 비로그인 사용자는 본인이 찜했는지 여부를 판단할 수 없으므로 항상 false
        if (userId == null) {
            return new BookmarkStatus(false, totalCount);
        }

        SupporterProfile supporter = supporterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("서포터 프로필을 찾을 수 없습니다."));

        boolean bookmarked = bookmarkRepository.existsBySupporterAndProject(supporter, project);
        return new BookmarkStatus(bookmarked, totalCount);
    }

    // 한글 설명: 프로젝트를 찜한다. 이미 찜 상태라면 예외 없이 그대로 유지한다.
    public BookmarkStatus bookmark(Long userId, Long projectId) {
        if (userId == null) {
            throw new IllegalStateException("로그인한 사용자만 프로젝트를 찜할 수 있습니다.");
        }

        SupporterProfile supporter = supporterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("서포터 프로필을 찾을 수 없습니다."));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("프로젝트를 찾을 수 없습니다."));

        boolean already = bookmarkRepository.existsBySupporterAndProject(supporter, project);
        if (!already) {
            bookmarkRepository.save(SupporterBookmarkProject.of(supporter, project));
        }

        long totalCount = bookmarkRepository.countByProject(project);
        return new BookmarkStatus(true, totalCount);
    }

    // 한글 설명: 프로젝트 찜을 해제한다. 이미 없는 상태여도 조용히 지나간다.
    public BookmarkStatus unbookmark(Long userId, Long projectId) {
        if (userId == null) {
            throw new IllegalStateException("로그인한 사용자만 프로젝트 찜을 해제할 수 있습니다.");
        }

        SupporterProfile supporter = supporterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("서포터 프로필을 찾을 수 없습니다."));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("프로젝트를 찾을 수 없습니다."));

        bookmarkRepository.findBySupporterAndProject(supporter, project)
                .ifPresent(bookmarkRepository::delete);

        long totalCount = bookmarkRepository.countByProject(project);
        return new BookmarkStatus(false, totalCount);
    }

    // 한글 설명: 컨트롤러/프로젝트 상세에서 사용하기 위한 찜 상태 요약 DTO.
    public record BookmarkStatus(
            boolean bookmarked,
            long bookmarkCount
    ) {}
}
