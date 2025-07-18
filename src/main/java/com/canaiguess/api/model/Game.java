package com.canaiguess.api.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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

    private User user;

    private int currentBatch;

    private boolean finished; // derived from batches and currentBatch

    private int pointsEarned; // needed for last 10 user games

    private int score;

    @CreationTimestamp
    private LocalDateTime createdAt; // needed for last 10 user games
}
