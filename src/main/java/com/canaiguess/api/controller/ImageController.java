package com.canaiguess.api.controller;

import com.canaiguess.api.dto.HintResponseDTO;
import com.canaiguess.api.dto.SubmitReportRequestDTO;
import com.canaiguess.api.dto.UploadImageRequestDTO;
import com.canaiguess.api.model.Image;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.ImageRepository;
import com.canaiguess.api.service.GeminiService;
import com.canaiguess.api.service.ImageReportService;
import com.canaiguess.api.service.R2UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/image")
@Tag(name = "Image", description = "Image operations including AI analysis")
@AllArgsConstructor
public class ImageController {

    private final GeminiService geminiService;
    private final ImageRepository imageRepository;
    private final ImageReportService imageReportService;
    private final R2UploadService uploadService;

    @Operation(summary = "Get a hint for an image")
    @PostMapping("/{imageId}/hint")
    public ResponseEntity<HintResponseDTO> getHint(
            @PathVariable String imageId,
            @AuthenticationPrincipal User user
    ) {
        Image image = imageRepository.findByPublicId(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        HintResponseDTO result = geminiService.analyzeImagePrompt(image.getUrl());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Report an image")
    @PostMapping("/{imageId}/report")
    public ResponseEntity<Void> reportImage(
            @PathVariable String imageId,
            @RequestBody SubmitReportRequestDTO dto,
            @AuthenticationPrincipal User user
    ) {
        imageReportService.submitReport(imageId, user, dto);
        return ResponseEntity.noContent().build();

    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Upload an image (ADMIN)")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
            @ModelAttribute UploadImageRequestDTO request, // cant use path variable for files
            @AuthenticationPrincipal User user
    ) {
        String url = uploadService.uploadAndSave(request.getFile(), request.isFake());
        return ResponseEntity.ok(url);
    }


}
