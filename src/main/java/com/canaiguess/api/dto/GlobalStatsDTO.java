package com.canaiguess.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GlobalStatsDTO
{
    private int totalImages;
    private double globalAccuracy;
    private int totalUsers;
    private String hardestImageUrl;
    private double hardestImageAccuracy;
    private int totalHintsTaken;
    private long totalGamesPlayed;
}
