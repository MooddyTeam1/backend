package com.mooddy.backend.external.itunes.service;

import com.mooddy.backend.external.itunes.dto.ItunesSearchResultDto;
import com.mooddy.backend.feature.track.domain.Track;

public interface ItunesService {
    ItunesSearchResultDto search(String query);

    Track getOrCreateTrackEntity(Long trackId);
}
