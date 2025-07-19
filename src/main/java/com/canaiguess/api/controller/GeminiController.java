package com.canaiguess.api.controller;

import com.canaiguess.api.dto.PromptRequestDTO;
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
@RequestMapping("/api/ai")
@Tag(name = "AI Analysis", description = "Use Gemini to analyze images and answer prompts")
public class GeminiController {

    private final GeminiService geminiService;
    private final ImageRepository imageRepository;

    public GeminiController(GeminiService geminiService, ImageRepository imageRepository) {
        this.geminiService = geminiService;
        this.imageRepository = imageRepository;
    }

    @Operation(
            summary = "Analyze image with Gemini",
            description = "Takes an image ID and a prompt, runs it through Gemini vision model, and returns a response"
    )
    @PostMapping("/hint")
    public ResponseEntity<String> analyze(
            @RequestBody PromptRequestDTO request,
            @AuthenticationPrincipal User user) {
        Image image = imageRepository.findById(request.getImageId())
                .orElseThrow(() -> new RuntimeException("Image not found"));

        String imageUrl = image.getFilename();
        String prompt = request.getPrompt();

        String result = geminiService.analyzeImagePrompt(imageUrl, prompt);
        return ResponseEntity.ok(result);
    }
}
