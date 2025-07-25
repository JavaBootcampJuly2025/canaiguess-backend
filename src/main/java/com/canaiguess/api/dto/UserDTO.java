package com.canaiguess.api.dto;

import com.canaiguess.api.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserDTO
{
    private String username;
    private Integer score;           // total points
    private Double accuracy;     // avg accuracy
    private Integer totalGuesses;    // all guesses ever
    private Integer correctGuesses;  // all correct guesses ever
    private Integer totalGames;      // total games played
    private String role;

    public static UserDTO from(User user, Integer totalGames) {
        double accuracy = user.getTotalGuesses() > 0
            ? (double) user.getCorrectGuesses() / user.getTotalGuesses()
            : 0.0;

        return UserDTO.builder()
            .username(user.getUsername())
            .score(user.getScore())
            .accuracy(accuracy)
            .totalGuesses(user.getTotalGuesses())
            .correctGuesses(user.getCorrectGuesses())
            .totalGames(totalGames)
            .role(String.valueOf(user.getRole()))
            .build();
    }

}
