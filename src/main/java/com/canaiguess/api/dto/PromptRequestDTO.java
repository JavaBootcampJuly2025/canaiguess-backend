package com.canaiguess.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromptRequestDTO {
    private String imageUrl;
    private String prompt; // "Is this real or AI-generated?"
}
