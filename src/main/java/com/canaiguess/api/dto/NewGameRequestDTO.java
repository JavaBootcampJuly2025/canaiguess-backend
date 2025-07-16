package com.canaiguess.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewGameRequestDTO {
    private int batchCount;
    private int batchSize;
    private int difficulty;
}
