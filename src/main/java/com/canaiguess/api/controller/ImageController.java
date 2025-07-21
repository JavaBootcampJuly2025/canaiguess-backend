package com.canaiguess.api.controller;

import com.canaiguess.api.dto.HintResponseDTO;
import com.canaiguess.api.model.Image;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.ImageRepository;
import com.canaiguess.api.service.GeminiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/image")
@Tag(name = "Image", description = "Image operations including AI analysis")
public class ImageController {

    private final GeminiService geminiService;
    private final ImageRepository imageRepository;

    public ImageController(GeminiService geminiService, ImageRepository imageRepository) {
        this.geminiService = geminiService;
        this.imageRepository = imageRepository;
    }

    @Operation(
            summary = "Get a hint for an image",
            description = "Analyzes the given image using Gemini and returns a response to the user's prompt."
    )
    @PostMapping("/{imageId}/hint")
    public ResponseEntity<HintResponseDTO> getHint(
            @PathVariable Long imageId,
            @AuthenticationPrincipal User user
    ) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        HintResponseDTO result = geminiService.analyzeImagePrompt(image.getFilename());
        return ResponseEntity.ok(result);
    }
}
