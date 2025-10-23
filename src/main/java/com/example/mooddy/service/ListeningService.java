package com.example.mooddy.service;

import com.example.mooddy.domain.ListeningHistory;
import com.example.mooddy.repository.ListeningHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListeningService {

    private final ListeningHistoryRepository listeningRepository;

    public ListeningHistory saveListening(String email, String trackName, String artist, String genre) {
        ListeningHistory history = ListeningHistory.builder()
                .email(email)
                .trackName(trackName)
                .artist(artist)
                .genre(genre)
                .listenedAt(LocalDateTime.now())
                .build();
        return listeningRepository.save(history);
    }

    public List<ListeningHistory> getRecentListening(String email) {
        return listeningRepository.findTop10ByEmailOrderByListenedAtDesc(email);
    }

    public Map<String, Long> getGenreStats(String email) {
        List<ListeningHistory> history = listeningRepository.findByEmail(email);
        return history.stream()
                .collect(Collectors.groupingBy(ListeningHistory::getGenre, Collectors.counting()));
    }
}
