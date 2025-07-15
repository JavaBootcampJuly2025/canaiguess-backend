package com.canaiguess.api;



import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class Points {
    private String gameMode;
    private int accuracy;
    private int difficulty;

    public Points(String gameMode, int accuracy, int difficulty) {
        this.gameMode = gameMode;
        this.accuracy = accuracy;
        this.difficulty = difficulty;
    }

    public int calculateScore() {
        int baseScore = 100;
        double accuracyRatio = accuracy / 100.0; // Convert to 0.0 - 1.0

        return (int) (baseScore * difficulty * accuracyRatio);
    }

    public static void main(String[] args) {
        Points p = new Points();
        int score = p.calculateScore();
        System.out.println("Score: " + score);
    }
}