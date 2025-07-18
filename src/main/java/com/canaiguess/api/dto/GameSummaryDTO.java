package com.canaiguess.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameSummaryDTO {
    private Long gameId;
    private int pointsEarned;
}
