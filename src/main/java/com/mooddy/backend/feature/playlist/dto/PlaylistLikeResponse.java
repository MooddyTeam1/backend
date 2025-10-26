package com.mooddy.backend.feature.playlist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PlaylistLikeResponse {
    private Long playlistId;
    private boolean liked;
    private Long likeCount;
}
