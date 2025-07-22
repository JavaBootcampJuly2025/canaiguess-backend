package com.canaiguess.api.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HintResponseDTO {
    private boolean fake;
    private List<String> signs;

}
