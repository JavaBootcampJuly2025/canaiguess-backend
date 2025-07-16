package com.canaiguess.api.model;

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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String gameMode;

    private int batchCount;

    private int batchSize;

    private int difficulty;

    private String userId;

    private int currentBatch;

    private boolean finished; // derived from batches and currentBatch

}
