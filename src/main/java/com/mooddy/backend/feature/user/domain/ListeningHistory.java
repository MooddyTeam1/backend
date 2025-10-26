package com.mooddy.backend.feature.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "listening_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListeningHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String trackName;
    private String artist;
    private String genre;

    private LocalDateTime listenedAt;
}
