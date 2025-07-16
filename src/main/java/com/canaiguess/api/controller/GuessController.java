package com.canaiguess.api.controller;

import com.canaiguess.api.dto.GuessRequestDTO;
import com.canaiguess.api.dto.GuessResultDTO;
import com.canaiguess.api.service.ImageGameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public class GuessController {

    private final ImageGameService imageGameService;
    public GuessController(ImageGameService imageGameService) {
        this.imageGameService = imageGameService;
    }

    @PostMapping("/api/guess")
    public ResponseEntity<GuessResultDTO> validateGuesses(@RequestBody GuessRequestDTO guessRequest) {
        List<Boolean> results = imageGameService.validateGuesses(guessRequest.getImages(), guessRequest.getGuesses());
        return ResponseEntity.ok(new GuessResultDTO(results));
    }

}
