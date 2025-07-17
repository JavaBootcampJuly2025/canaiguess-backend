package com.canaiguess.api.controller;

import com.canaiguess.api.dto.GuessRequestDTO;
import com.canaiguess.api.dto.GuessResultDTO;
import com.canaiguess.api.service.GameSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/guess")
@Tag(name = "Guess", description = "Endpoints for validating image guesses")
public class GuessController {

    private final GameSessionService gameSessionService;

    public GuessController(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @PostMapping
    @Operation(
            summary = "Validate guesses",
            description = "Validates user's guesses for a batch of images. Returns which were guessed correctly."
    )
    public ResponseEntity<GuessResultDTO> validateGuesses(@RequestBody GuessRequestDTO guessRequest) {
        List<Boolean> results = gameSessionService.validateGuesses(guessRequest.getImages(), guessRequest.getGuesses());
        return ResponseEntity.ok(new GuessResultDTO(results));
    }

}
