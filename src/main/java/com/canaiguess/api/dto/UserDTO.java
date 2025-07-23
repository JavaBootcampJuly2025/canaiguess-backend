package com.canaiguess.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserDTO
{
    private String username;
    private int score;           // total points
    private Double accuracy;     // avg accuracy
    private int totalGuesses;    // all guesses ever
    private int correctGuesses;  // all correct guesses ever
    private int totalGames;      // total games played
}
