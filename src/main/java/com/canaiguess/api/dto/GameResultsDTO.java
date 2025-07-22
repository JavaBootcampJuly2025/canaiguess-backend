package com.canaiguess.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GameResultsDTO {
    private String id;
    private Integer correct;
    private Integer incorrect;
    private Double accuracy;
    private Integer score;
    private LocalDateTime createdAt;

}