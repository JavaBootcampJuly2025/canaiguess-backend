package com.canaiguess.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccuracyLeaderboardDTO {
    private String username;
    private double accuracy;
}
