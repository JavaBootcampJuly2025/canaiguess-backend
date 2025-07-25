package com.canaiguess.api.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadImageRequestDTO {
    private MultipartFile file;
    private boolean fake; // true = AI, false = real
}
