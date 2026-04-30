package com.moa.backend.global.init;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.project.entity.ProjectResultStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.reward.entity.Reward;
import com.moa.backend.domain.reward.entity.RewardDisclosureCategory;
import com.moa.backend.domain.inventory.redis.StockSyncService;
import com.moa.backend.domain.reward.repository.RewardRepository;
import com.moa.backend.domain.user.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestDataInitController {

    private static final int LOAD_TEST_USER_COUNT = 1000;

    private final UserSeedingService userSeedingService;
    private final MakerRepository makerRepository;
    private final ProjectRepository projectRepository;
    private final RewardRepository rewardRepository;
    private final StockSyncService stockSyncService;

    @PostMapping("/public/test-init")
    @Transactional
    public String initTestData() {
        User makerOwner = userSeedingService.ensureMakerSeedUser();
        int newUsers = userSeedingService.seedLoadTestUsers(LOAD_TEST_USER_COUNT);

        Maker maker =
                makerRepository
                        .findByOwner_Id(makerOwner.getId())
                        .orElseGet(
                                () ->
                                        makerRepository.save(
                                                Maker.createIndividual(makerOwner, "MOA 테크랩 스튜디오")));

        Optional<Project> existingProject = projectRepository.findByTitle(K6LoadTestConstants.PROJECT_TITLE);
        if (existingProject.isPresent()) {
            Project p = existingProject.get();
            Long rewardId =
                    rewardRepository.findByProject_Id(p.getId()).stream()
                            .findFirst()
                            .map(Reward::getId)
                            .orElse(null);
            return String.format(
                    "⚠️ 이미 데이터가 있습니다. Project ID: %d, Reward ID: %d | 이번에 추가된 로드유저: %d명",
                    p.getId(), rewardId, newUsers);
        }

        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(14);

        Project project =
                Project.builder()
                        .maker(maker)
                        .title(K6LoadTestConstants.PROJECT_TITLE)
                        .summary(
                                "국내 기술 스타트업 MOA 테크가 선보이는 차세대 웨어러블. 심박·수면·GPS를 한 번에, 14일 배터리와 "
                                        + "5ATM 방수를 지원합니다. 본 펀딩은 K6 동시성·재고 검증 전용 테스트 프로젝트입니다.")
                        .storyMarkdown(
                                "## 프로젝트 소개\n\n"
                                        + "- **타깃**: 직장인·러너 겸용 라이프스타일 워치\n"
                                        + "- **목표 금액**: 펀딩 성공 시 양산 투입\n\n"
                                        + "## 일정\n\n"
                                        + "| 구분 | 내용 |\n"
                                        + "|------|------|\n"
                                        + "| 펀딩 | 오늘 기준 2주 |\n"
                                        + "| 배송 | 펀딩 종료 후 순차 발송 |\n\n"
                                        + "## 유의사항\n\n"
                                        + "실제 리워드가 아닌 **부하 테스트**용 데이터일 수 있습니다.\n")
                        .goalAmount(50_000_000L)
                        .startDate(start)
                        .endDate(end)
                        .category(Category.TECH)
                        .lifecycleStatus(ProjectLifecycleStatus.LIVE)
                        .reviewStatus(ProjectReviewStatus.APPROVED)
                        .resultStatus(ProjectResultStatus.NONE)
                        .coverImageUrl("https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&w=1200")
                        .tags(List.of("웨어러블", "테크", "한정판", "K6테스트"))
                        .liveStartAt(LocalDateTime.now().minusDays(1))
                        .liveEndAt(end.atTime(23, 59, 59))
                        .build();
        projectRepository.save(project);

        Reward reward =
                Reward.builder()
                        .project(project)
                        .name("얼리버드 — 선착순 100세트 한정 (스마트 워치 본체 + 스트랩)")
                        .description(
                                "본 리워드는 **선착순 100개** 한정입니다. 동일 시각 다수 주문 시 서버·DB 부하 및 재고 차감 "
                                        + "동시성이 검증됩니다.\n\n"
                                        + "- 구성: 본체 1, 실리콘 스트랩 1, 충전독 1\n"
                                        + "- 색상: 미드나이트 블랙 고정\n"
                                        + "- A/S: 국내 1년 무상(부품별 상이)")
                        .price(99_000L)
                        .stockQuantity(K6LoadTestConstants.REWARD_INITIAL_STOCK)
                        .active(true)
                        .estimatedDeliveryDate(end.plusMonths(2))
                        .disclosureCategory(RewardDisclosureCategory.JEWELRY.name())
                        .build();
        rewardRepository.save(reward);
        stockSyncService.syncSingleRewardEntity(reward);

        return String.format(
                "✅ 세팅 완료. Project ID: %d, Reward ID: %d | 신규 로드유저: %d명 | K6 CONFIG에 위 ID를 넣으세요.",
                project.getId(), reward.getId(), newUsers);
    }
}
