package com.mooddy.backend.external.itunes.service;

import com.mooddy.backend.feature.track.domain.Track;
import com.mooddy.backend.feature.track.dto.TrackSearchResponseDto;

import java.util.List;

public interface ItunesService {
    List<TrackSearchResponseDto> searchTracks(String query);

    Track getOrCreateTrackEntity(Long trackId);
}
