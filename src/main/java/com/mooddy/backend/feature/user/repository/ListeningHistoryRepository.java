package com.mooddy.backend.feature.user.repository;


import com.mooddy.backend.feature.user.domain.ListeningHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListeningHistoryRepository extends JpaRepository<ListeningHistory, Long> {
    List<ListeningHistory> findByEmail(String email);
    List<ListeningHistory> findTop10ByEmailOrderByListenedAtDesc(String email);
}
