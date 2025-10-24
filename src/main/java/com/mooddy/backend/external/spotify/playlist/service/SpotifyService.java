package com.mooddy.backend.external.spotify.playlist.service;

import com.mooddy.backend.feature.playlist.domain.Playlist;

import java.util.List;

public interface SpotifyService {

    public List<Playlist> getSpotifyPlaylists(String spotifyAccessToken, Long userId);

}
