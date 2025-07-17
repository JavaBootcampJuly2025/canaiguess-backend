package com.canaiguess.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameResultsDTO {
    private int correct;
    private int incorrect;
    private double accuracy;
}