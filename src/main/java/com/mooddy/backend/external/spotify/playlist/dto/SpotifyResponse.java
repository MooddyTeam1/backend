package com.mooddy.backend.external.spotify.playlist.dto;

import com.mooddy.backend.feature.playlist.domain.Playlist;
import com.mooddy.backend.feature.playlist.domain.Visibility;
import com.mooddy.backend.feature.user.domain.User;
import lombok.Data;

@Data
public class SpotifyResponse {
    private String id;
    private String name;
    private String description;
    private String img;
    private boolean isPublic;

    public Playlist toEntity(User user) {
        return Playlist.builder()
                .title(this.name)
                .description(this.description)
                .coverImageUrl(this.img)
                .visibility(this.isPublic ? Visibility.PUBLIC : Visibility.PRIVATE)
                .user(user)
                .build();
    }
}
