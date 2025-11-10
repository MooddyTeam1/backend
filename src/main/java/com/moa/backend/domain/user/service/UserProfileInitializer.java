package com.moa.backend.domain.user.service;


import com.moa.backend.domain.user.entity.Maker;
import com.moa.backend.domain.user.entity.SupporterProfile;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.MakerRepository;
import com.moa.backend.domain.user.repository.SupporterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileInitializer {

    private final SupporterProfileRepository supporterProfileRepository;
    private final MakerRepository makerRepository;

    @Transactional
    public void initializeFor(User user) {
        if (user == null || user.getId() == null) {
            return;
        }

        if (!supporterProfileRepository.existsByUserId(user.getId())) {
            supporterProfileRepository.save(SupporterProfile.createEmpty(user));
        }

        if (!makerRepository.existsByOwner_Id(user.getId())) {
            makerRepository.save(Maker.createDefault(user));
        }
    }
}