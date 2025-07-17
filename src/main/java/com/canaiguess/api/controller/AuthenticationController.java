package com.canaiguess.api.controller;

import com.canaiguess.api.dto.AuthenticationRequest;
import com.canaiguess.api.dto.AuthenticationResponse;
import com.canaiguess.api.dto.RegisterRequest;
import com.canaiguess.api.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Handles user registration and authentication")
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user and returns a JWT token."
    )
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    @Operation(
            summary = "Authenticate a user",
            description = "Validates user credentials and returns a JWT token."
    )
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
