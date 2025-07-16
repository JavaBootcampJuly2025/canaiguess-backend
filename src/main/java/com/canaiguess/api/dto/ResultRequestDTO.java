package com.canaiguess.api.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ResultRequestDTO {
    private List<String> imageFilenames;
    private List<String> guesses;
}

