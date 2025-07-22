package com.canaiguess.api.controller;

import com.canaiguess.api.dto.CaptchaRequestDTO;
import com.canaiguess.api.dto.CaptchaResponseDTO;
import com.canaiguess.api.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/captcha")
@RequiredArgsConstructor
@Tag(name = "CAPTCHA", description = "Handles CAPTCHA verification")
public class CaptchaController {
    private final CaptchaService captchaService;

    @PostMapping("/verify")
    public ResponseEntity<CaptchaResponseDTO> verifyCaptcha(@RequestBody CaptchaRequestDTO request) {
        CaptchaResponseDTO response = captchaService.verifyCaptcha(request);
        return ResponseEntity.ok(response);
    }
}
