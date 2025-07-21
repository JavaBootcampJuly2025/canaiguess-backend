package com.canaiguess.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LastGameDTO {
    private String id;
    private int pointsEarned;
    private LocalDateTime createdAt;
}
