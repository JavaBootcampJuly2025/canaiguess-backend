package com.canaiguess.api.model;




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