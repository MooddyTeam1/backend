package com.moa.backend.domain.user.repository;

import com.moa.backend.domain.user.entity.CreatorStatus;
import com.moa.backend.domain.user.entity.User;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}

