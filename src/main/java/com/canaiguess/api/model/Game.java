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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int batchCount;

    private int batchSize;

    private int difficulty;

    private Long userId;

    private int currentBatch;

    private boolean finished; // derived from batches and currentBatch

}
