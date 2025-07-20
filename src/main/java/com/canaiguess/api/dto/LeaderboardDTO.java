package com.canaiguess.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaderboardDTO {
    private String username;
    private int score;         // for points leaderboard
    private Double accuracy;   // for accuracy leaderboard
}
