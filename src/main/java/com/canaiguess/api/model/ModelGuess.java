package com.canaiguess.api.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelGuess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private boolean fake;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "model_guess_signs", joinColumns = @JoinColumn(name = "model_guess_id"))
    @Column(name = "sign")
    private List<String> signs;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;
}
