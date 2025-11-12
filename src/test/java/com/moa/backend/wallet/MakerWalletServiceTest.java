package com.moa.backend.wallet;

import static org.assertj.core.api.Assertions.assertThat;

import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.settlement.entity.Settlement;
import com.moa.backend.domain.settlement.repository.SettlementRepository;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.domain.wallet.entity.MakerWallet;
import com.moa.backend.domain.wallet.entity.WalletTransaction;
import com.moa.backend.domain.wallet.entity.WalletTransactionType;
import com.moa.backend.domain.wallet.repository.MakerWalletRepository;
import com.moa.backend.domain.wallet.repository.WalletTransactionRepository;
import com.moa.backend.domain.wallet.service.MakerWalletService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MakerWalletServiceTest {

    @Autowired
    private MakerWalletService makerWalletService;
    @Autowired
    private MakerWalletRepository makerWalletRepository;
    @Autowired
    private WalletTransactionRepository walletTransactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MakerRepository makerRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private SettlementRepository settlementRepository;

    @Test
    void 선지급_적립시_가용잔액과_거래로그확인() {
        // given: 메이커/정산 데이터 준비
        Maker maker = prepareMaker();
        makerWalletService.createForMaker(maker);
        Settlement settlement = createSettlement(maker);

        // when: 선지급 금액 적립
        makerWalletService.creditAvailable(maker, 50_000L, settlement);

        // then: 가용 잔액 + 총 벌이 금액이 증가하고 로그가 남는다
        MakerWallet wallet = makerWalletRepository.findByMakerId(maker.getId()).orElseThrow();
        assertThat(wallet.getAvailableBalance()).isEqualTo(50_000L);
        assertThat(wallet.getTotalEarned()).isEqualTo(50_000L);

        WalletTransaction tx = walletTransactionRepository.findAll().get(0);
        assertThat(tx.getType()).isEqualTo(WalletTransactionType.SETTLEMENT_FIRST);
        assertThat(tx.getAmount()).isEqualTo(50_000L);
    }

    @Test
    void 잔금지급시_pending에서_available로() {
        // given: pending 금액 준비
        Maker maker = prepareMaker();
        makerWalletService.createForMaker(maker);
        Settlement settlement = createSettlement(maker);

        makerWalletService.addPending(maker, 80_000L);
        // when: 잔금 확정 처리
        makerWalletService.releasePendingToAvailable(maker, 80_000L, settlement);

        // then: pending은 0, available은 증가
        MakerWallet wallet = makerWalletRepository.findByMakerId(maker.getId()).orElseThrow();
        assertThat(wallet.getPendingBalance()).isZero();
        assertThat(wallet.getAvailableBalance()).isEqualTo(80_000L);

        WalletTransaction tx = walletTransactionRepository.findAll().get(0);
        assertThat(tx.getType()).isEqualTo(WalletTransactionType.SETTLEMENT_FINAL);
        assertThat(tx.getAmount()).isEqualTo(80_000L);
    }

    @Test
    void 환불회수시_available감소() {
        // given: 가용 잔액 60,000 확보
        Maker maker = prepareMaker();
        makerWalletService.createForMaker(maker);
        Settlement settlement = createSettlement(maker);

        makerWalletService.creditAvailable(maker, 60_000L, settlement);
        // when: 환불 회수
        makerWalletService.refundDebit(maker, 30_000L);

        // then: 가용 잔액이 회수 금액만큼 감소
        MakerWallet wallet = makerWalletRepository.findByMakerId(maker.getId()).orElseThrow();
        assertThat(wallet.getAvailableBalance()).isEqualTo(30_000L);
        assertThat(wallet.getTotalEarned()).isEqualTo(30_000L);

        List<WalletTransaction> logs = walletTransactionRepository.findAll();
        assertThat(logs).hasSize(2);
        WalletTransaction refundLog = logs.stream()
                .filter(tx -> tx.getType() == WalletTransactionType.REFUND_DEBIT)
                .findFirst()
                .orElseThrow();
        assertThat(refundLog.getAmount()).isEqualTo(-30_000L);
    }

    private Maker prepareMaker() {
        User user = userRepository.save(User.createUser(
                "maker-" + UUID.randomUUID() + "@moa.com",
                "encodedPw",
                "메이커유저"
        ));
        return makerRepository.save(Maker.create(user, "메이커-" + UUID.randomUUID()));
    }

    private Settlement createSettlement(Maker maker) {
        Project project = projectRepository.save(Project.builder()
                .maker(maker)
                .title("메이커 프로젝트 " + UUID.randomUUID())
                .goalAmount(300_000L)
                .build());

        Settlement settlement = Settlement.create(
                project,
                maker,
                100_000L,
                5_000L,
                5_000L,
                90_000L,
                45_000L,
                45_000L
        );
        return settlementRepository.save(settlement);
    }
}
