package com.canaiguess.api.model;

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

}
