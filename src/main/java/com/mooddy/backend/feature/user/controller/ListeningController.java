package com.mooddy.backend.feature.user.controller;

import com.mooddy.backend.feature.user.domain.ListeningHistory;
import com.mooddy.backend.feature.user.service.ListeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/listening")
@RequiredArgsConstructor
public class ListeningController {

    private final ListeningService listeningService;

    // 청취 기록 저장
    // 최근 청취 목록 조회
    // 장르별 통계 조회
    @PostMapping("/track")
    public ResponseEntity<ListeningHistory> saveListening(
            @RequestParam String email,
            @RequestParam String trackName,
            @RequestParam String artist,
            @RequestParam String genre
    ) {
        ListeningHistory history = listeningService.saveListening(email, trackName, artist, genre);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{email}/recent")
    public ResponseEntity<List<ListeningHistory>> getRecentListening(@PathVariable String email) {
        List<ListeningHistory> history = listeningService.getRecentListening(email);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{email}/stats")
    public ResponseEntity<Map<String, Long>> getGenreStats(@PathVariable String email) {
        Map<String, Long> stats = listeningService.getGenreStats(email);
        return ResponseEntity.ok(stats);
    }
}
