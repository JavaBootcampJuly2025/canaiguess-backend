package com.canaiguess.api.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int batchCount;

    private int batchSize;

    private int difficulty;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int currentBatch;

    private boolean finished; // derived from batches and currentBatch

    @OneToMany(mappedBy = "game")
    private List<ImageGame> imageGames;

    @Column(nullable = false)
    private int score = 0; // needed for last 10 user games

    @CreationTimestamp
    private LocalDateTime createdAt; // needed for last 10 user games
}
