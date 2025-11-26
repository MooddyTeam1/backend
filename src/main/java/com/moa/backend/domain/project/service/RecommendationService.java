package com.moa.backend.domain.project.service;

import com.moa.backend.domain.onboarding.model.BudgetRange;
import com.moa.backend.domain.project.dto.ProjectListResponse;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.user.entity.SupporterProfile;
import com.moa.backend.domain.user.repository.SupporterProfileRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final SupporterProfileRepository supporterProfileRepository;
    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public List<ProjectListResponse> recommend(Long userId) {

        SupporterProfile profile = supporterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.SUPPORTER_PROFILE_NOT_FOUND));

        // 관심 카테고리 추출 (대문자 정규화)
        List<String> categories = profile.getInterestCategories().stream()
                .filter(java.util.Objects::nonNull)
                .map(s -> s.trim().toUpperCase())
                .toList();

        if (categories.isEmpty()) {
            return List.of();
        }

        // 후보 조회
        List<Project> candidates = projectRepository.findRecommendedByCategories(categories);

        // 점수 기반 정렬 후 카드 DTO 매핑
        return candidates.stream()
                .map(p -> new ScoredProject(p, score(profile, p)))
                .sorted(Comparator.comparingDouble(ScoredProject::score).reversed())
                .limit(30)
                .map(ScoredProject::project)
                .map(ProjectListResponse::fromRecommendation)
                .toList();
    }

    // ===========================
    // 점수 계산 (스타일/예산/인기)
    // ===========================

    private double score(SupporterProfile profile, Project project) {
        double score = 50; // 기본 가점 (카테고리 매칭)

        // 스타일 매칭 가점
        if (matchesPreferredStyle(profile, project)) {
            score += 15;
        }

        // 예산 가점
        score += budgetScore(profile, project);

        // 인기 가점
        score += popularityScore(project);

        return score;
    }

    // 인기 = 결제 완료 주문 수 기반 가점 (최대 15)
    private double popularityScore(Project project) {
        int count = projectRepository.countSupporters(project.getId());
        return Math.min(count / 5.0, 15);
    }

    // 스타일 매칭 (제목/스토리 텍스트 + 태그 + 스타일 키워드 매핑)
    private boolean matchesPreferredStyle(SupporterProfile profile, Project project) {
        List<String> styles = profile.getPreferredStylesList(); // ex) ["PRACTICAL","FNB"]
        if (styles == null || styles.isEmpty()) return false;

        // 제목 + 스토리 텍스트
        StringBuilder content = new StringBuilder();
        if (project.getTitle() != null) content.append(project.getTitle()).append(" ");
        if (project.getStoryMarkdown() != null) content.append(project.getStoryMarkdown());
        String text = content.toString().toUpperCase().replaceAll("\\s+", " ");

        // 프로젝트 태그 (정규화: 대문자 + 공백 제거)
        List<String> tags = project.getTags() == null ? List.of() :
                project.getTags().stream()
                        .filter(java.util.Objects::nonNull)
                        .map(RecommendationService::norm)
                        .toList();

        return styles.stream().anyMatch(styleName -> {
            String style = styleName.toUpperCase();  // ex) "PRACTICAL"

            // 1) 제목/스토리에 직접 포함
            if (text.contains(style)) return true;

            // 2) 태그에 직접 포함
            if (tags.contains(style)) return true;

            // 3) 스타일 키워드 매핑 비교 (정규화 후 contains)
            List<String> keywords = STYLE_TAGS_MAP.getOrDefault(style, List.of());
            return tags.stream().anyMatch(projectTag ->
                    keywords.stream().map(RecommendationService::norm).anyMatch(projectTag::contains)
            );
        });
    }

    // 예산 가점
    private double budgetScore(SupporterProfile profile, Project project) {
        if (profile.getBudgetRange() == null || profile.getBudgetRange() == BudgetRange.NO_PREFERENCE) {
            return 0;
        }

        int limit = budgetUpperLimit(profile.getBudgetRange());
        Long goal = project.getGoalAmount();
        if (goal == null) return 0;
        return goal <= limit ? 20 : 0;
    }

    private int budgetUpperLimit(BudgetRange range) {
        return switch (range) {
            case UNDER_50K -> 50_000;
            case BETWEEN_50K_100K -> 100_000;
            case BETWEEN_100K_200K -> 200_000;
            case BETWEEN_200K_500K -> 500_000;
            case ABOVE_500K -> Integer.MAX_VALUE;
            default -> Integer.MAX_VALUE;
        };
    }

    private record ScoredProject(Project project, double score) {}

    // 문자열 정규화: 대문자 + 공백 제거
    private static String norm(String s) {
        return s == null ? "" : s.toUpperCase().replaceAll("\\s+", "");
    }

    // 스타일-키워드 매핑 상수 (정규화 비교 전용)
    private static final Map<String, List<String>> STYLE_TAGS_MAP = Map.of(
            "PRACTICAL", List.of("일상", "수납", "정리", "실용", "필수", "가성비", "활용"),
            "UNIQUE_GOODS", List.of("디자인", "아트", "굿즈", "캐릭터", "한정판", "콜라보", "감성"),
            "FNB", List.of("음식", "식품", "식자재", "주방", "베이커리", "간편식", "식당"),
            "DIGITAL_SERVICE", List.of("앱", "서비스", "웹", "AI", "구독", "프로그램", "소프트웨어")
    );
}

