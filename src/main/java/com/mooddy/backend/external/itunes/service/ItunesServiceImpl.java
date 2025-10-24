package com.mooddy.backend.external.itunes.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mooddy.backend.external.itunes.dto.*;
import com.mooddy.backend.feature.track.domain.Track;
import com.mooddy.backend.feature.track.dto.TrackSearchResponseDto;
import com.mooddy.backend.feature.track.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItunesServiceImpl implements ItunesService {

    private final WebClient webClient;
    private final TrackRepository trackRepository;
    private final ObjectMapper objectMapper;
    private static final String ITUNES_SEARCH_URL = "https://itunes.apple.com/search";
    private static final String ITUNES_LOOKUP_URL = "https://itunes.apple.com/lookup";

    @Override
    public ItunesSearchResultDto search(String query) {
        log.info("iTunes 검색 시작 (WebClient) - query: {}", query);

        String uriString = UriComponentsBuilder.fromHttpUrl(ITUNES_SEARCH_URL)
                .queryParam("term", query)
                .queryParam("media", "music")
                .queryParam("entity", "musicTrack,album,musicArtist")
                .queryParam("limit", 10)
                .toUriString();

        try {
            ItunesResponse response = webClient.get()
                    .uri(uriString)
                    .retrieve()
                    .bodyToMono(ItunesResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            if (response == null || response.getResults() == null) {
                log.warn("iTunes API로부터 응답이 없거나 결과가 비어있습니다.");
                return new ItunesSearchResultDto(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()
                );
            }

            log.info("iTunes 검색 완료 - 결과 수: {}", response.getResultCount());

            // 결과를 wrapperType 기준으로 분류
            List<TrackSearchResponseDto> tracks = new ArrayList<>();
            List<AlbumSearchResponseDto> albums = new ArrayList<>();
            List<ArtistSearchResponseDto> artists = new ArrayList<>();

            for (Map<String, Object> result : response.getResults()) {
                String wrapperType = (String) result.get("wrapperType");

                if ("track".equals(wrapperType)) {
                    ItunesTrackDto trackDto = objectMapper.convertValue(result, ItunesTrackDto.class);
                    tracks.add(mapToTrackSearchResponseDto(trackDto));
                } else if ("collection".equals(wrapperType)) {
                    ItunesAlbumDto albumDto = objectMapper.convertValue(result, ItunesAlbumDto.class);
                    albums.add(mapToAlbumSearchResponseDto(albumDto));
                } else if ("artist".equals(wrapperType)) {
                    ItunesArtistDto artistDto = objectMapper.convertValue(result, ItunesArtistDto.class);
                    artists.add(mapToArtistSearchResponseDto(artistDto));
                }
            }

            return new ItunesSearchResultDto(tracks, albums, artists);

        } catch (Exception e) {
            log.error("iTunes 검색 실패 (WebClient)", e);
            throw new RuntimeException("Failed to search from iTunes", e);
        }
    }

    @Override
    @Transactional
    public Track getOrCreateTrackEntity(Long trackId) {
        ItunesTrackDto itunesTrack = fetchTrackFromApi(trackId);

        // 1차 조회: DB에 이미 있는지 확인
        Optional<Track> existingTrack = trackRepository.findByTrackId(trackId);
        if (existingTrack.isEmpty()) {
            existingTrack = trackRepository.findByTitleAndArtistAndAlbum(
                    itunesTrack.getTrackName(),  // title
                    itunesTrack.getArtistName() // artist
            );
        }

        if (existingTrack.isPresent()) {
            log.info("DB의 Track 사용 - trackId: {}", trackId);
            return existingTrack.get();
        }

        try {
            // iTunes API에서 정보 가져와서 저장
            log.info("iTunes API에서 Track 생성 - trackId: {}", trackId);
            Track track = mapToEntity(itunesTrack);
            return trackRepository.save(track);

        } catch (DataIntegrityViolationException e) {
            // 동시에 다른 요청이 이미 저장했을 경우
            log.warn("동시성 충돌 감지 - 다시 조회 시도: trackId={}", trackId);

            // 2차 조회: 다른 요청이 저장한 Track 가져오기
            return trackRepository.findByTrackId(trackId)
                    .orElseThrow(() -> new RuntimeException(
                            "Track 저장 실패 및 재조회 실패 - trackId: " + trackId));
        }
    }

    private ItunesTrackDto fetchTrackFromApi(Long trackId) {
        log.info("iTunes API로 곡 조회 - trackId: {}", trackId);
        String uriString = UriComponentsBuilder.fromHttpUrl(ITUNES_LOOKUP_URL)
                .queryParam("id", trackId)
                .toUriString();

        ItunesResponse response = webClient.get()
                .uri(uriString)
                .retrieve()
                .bodyToMono(ItunesResponse.class)
                .timeout(Duration.ofSeconds(5))
                .block();

        if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
            log.error("iTunes 곡 조회 실패 - trackId: {}", trackId);
            throw new RuntimeException("Failed to get track from iTunes API");
        }
        
        Map<String, Object> result = response.getResults().get(0);
        return objectMapper.convertValue(result, ItunesTrackDto.class);
    }

    private Track mapToEntity(ItunesTrackDto itunesTrack) {
        return Track.builder()
                .trackId(itunesTrack.getTrackId())
                .title(itunesTrack.getTrackName())
                .artist(itunesTrack.getArtistName())
                .album(itunesTrack.getCollectionName())
                .durationMs(itunesTrack.getTrackTimeMillis() != null ? itunesTrack.getTrackTimeMillis().intValue() : null)
                .albumCoverUrl(itunesTrack.getArtworkUrl100())
                .releaseDate(itunesTrack.getReleaseDate())
                .previewUrl(itunesTrack.getPreviewUrl())
                .primaryGenreName(itunesTrack.getPrimaryGenreName())
                .build();
    }

    private TrackSearchResponseDto mapToTrackSearchResponseDto(ItunesTrackDto itunesTrack) {
        return new TrackSearchResponseDto(
                String.valueOf(itunesTrack.getTrackId()),
                itunesTrack.getTrackName(),
                itunesTrack.getArtistName(),
                itunesTrack.getCollectionName(),
                itunesTrack.getTrackTimeMillis() != null ? itunesTrack.getTrackTimeMillis().intValue() : 0,
                itunesTrack.getArtworkUrl100(),
                itunesTrack.getReleaseDate(),
                itunesTrack.getPreviewUrl(),
                itunesTrack.getPrimaryGenreName()
        );
    }

    private AlbumSearchResponseDto mapToAlbumSearchResponseDto(ItunesAlbumDto albumDto) {
        return new AlbumSearchResponseDto(
                String.valueOf(albumDto.getCollectionId()),
                albumDto.getCollectionName(),
                albumDto.getArtistName(),
                albumDto.getArtworkUrl100(),
                albumDto.getTrackCount(),
                albumDto.getReleaseDate(),
                albumDto.getPrimaryGenreName()
        );
    }

    private ArtistSearchResponseDto mapToArtistSearchResponseDto(ItunesArtistDto artistDto) {
        return new ArtistSearchResponseDto(
                String.valueOf(artistDto.getArtistId()),
                artistDto.getArtistName(),
                artistDto.getPrimaryGenreName()
        );
    }
}

