package com.canaiguess.api;




import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor

public class Points {
    private int accuracy;
    private int difficulty;

    public int calculateScore() {
        return (int) (difficulty * accuracy);
    }
}