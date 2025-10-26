package com.mooddy.backend.external.itunes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * iTunes API에서 반환하는 Artist 정보를 받는 DTO
 * wrapperType이 "artist"인 경우 사용
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItunesArtistDto {
    /**
     * 응답 타입 (항상 "artist")
     */
    private String wrapperType;

    /**
     * 아티스트 타입 (보통 "Artist")
     */
    private String artistType;

    /**
     * 아티스트 고유 ID
     */
    private Long artistId;

    /**
     * 아티스트 이름
     */
    private String artistName;

    /**
     * 주요 장르명
     */
    private String primaryGenreName;

}
