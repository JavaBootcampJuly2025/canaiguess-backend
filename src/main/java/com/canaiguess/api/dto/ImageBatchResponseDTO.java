package com.canaiguess.api.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageBatchResponseDTO {
    private List<ImageDTO> images;
}
