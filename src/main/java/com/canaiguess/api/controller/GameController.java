package com.canaiguess.api.controller;

import com.canaiguess.api.dto.GameRequestDTO;
import com.canaiguess.api.dto.GameResponseDTO;
import com.canaiguess.api.service.GameService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

// @AuthenticationPrincipal possible
@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<GameResponseDTO> createGame(
            @RequestBody GameRequestDTO request
            //,@AuthenticationPrincipal User user
    ) {
        // For now, use a dummy userId; later, pull from logged-in user
        String userId = "dummy-user"; // TODO: Replace with real user id

        GameResponseDTO response = gameService.createGame(request, userId);
        return ResponseEntity.ok(response);
    }
}
