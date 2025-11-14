package com.moa.backend.domain.user.service;


import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.user.entity.SupporterProfile;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.SupporterProfileRepository;
import com.moa.backend.domain.wallet.service.MakerWalletService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileInitializer {

    private final SupporterProfileRepository supporterProfileRepository;
    private final MakerRepository makerRepository;
    private final MakerWalletService makerWalletService;

    @Transactional
    public void initializeFor(User user) {
        if (user == null || user.getId() == null) {
            return;
        }

        if (!supporterProfileRepository.existsByUserId(user.getId())) {
            supporterProfileRepository.save(SupporterProfile.createEmpty(user));
        }

        // 메이커 엔티티가 없으면 새로 만들고, 이어서 지갑까지 보장한다.
        makerRepository.findByOwner_Id(user.getId())
                .or(() -> Optional.of(makerRepository.save(Maker.create(user, user.getEmail()))))
                .ifPresent(makerWalletService::createForMaker);
    }
}
