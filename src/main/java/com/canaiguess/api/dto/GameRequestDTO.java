package com.canaiguess.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameRequestDTO {
    private String gameMode;
    private int batches;
    private int difficulty;
}
