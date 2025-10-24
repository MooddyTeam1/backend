package com.mooddy.backend.external.itunes.controller;

import com.mooddy.backend.external.itunes.dto.ItunesSearchResultDto;
import com.mooddy.backend.external.itunes.service.ItunesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/itunes")
@RequiredArgsConstructor
public class ItunesController {

    private final ItunesService itunesService;

    @GetMapping("/search")
    public ResponseEntity<ItunesSearchResultDto> search(@RequestParam("query") String query) {
        ItunesSearchResultDto result = itunesService.search(query);
        return ResponseEntity.ok(result);
    }
}
