package com.example.mooddy.repository;

import com.example.mooddy.domain.ListeningHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListeningHistoryRepository extends JpaRepository<ListeningHistory, Long> {
    List<ListeningHistory> findByEmail(String email);
    List<ListeningHistory> findTop10ByEmailOrderByListenedAtDesc(String email);
}
