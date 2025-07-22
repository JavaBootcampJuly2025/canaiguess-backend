package com.canaiguess.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ImageReportResponseDTO {
    private Long reportId;
    private String imageId;
    private String imageUrl;
    private String username;
    private String description;
    private Instant timestamp;
    private boolean resolved;
}
