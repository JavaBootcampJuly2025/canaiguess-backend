package com.canaiguess.api.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuessRequestDTO {
    private List<String> images;
    private List<Boolean> guesses;
}

