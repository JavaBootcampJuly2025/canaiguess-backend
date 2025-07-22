package com.canaiguess.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameDTO {
    private String id;
    private Integer correct;
    private Integer total;
    private Double accuracy;
    private Integer score;
    private LocalDateTime createdAt;

    private boolean finished;
    private int currentBatch;
    private int batchCount;
    private int batchSize;
    private int difficulty;
}

