package com.canaiguess.api.controller;

import com.canaiguess.api.dto.GameDTO;
import com.canaiguess.api.dto.GameRequestDTO;
import com.canaiguess.api.dto.GameResponseDTO;
import com.canaiguess.api.service.GameService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            //@AuthenticationPrincipal User user // TODO
    ) {
        //String userId = (user != null) ? user.getId() : null;

        GameResponseDTO response = gameService.createGame(request, null);
        return ResponseEntity.ok(response);
    }

    // TODO: check authorization as only logged in users may resume a game
    @GetMapping("/{gameId}")
    public ResponseEntity<GameDTO> getGameById(@PathVariable String gameId) {
        GameDTO game = gameService.getGameById(gameId);
        if (game != null) {
            return ResponseEntity.ok(game);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
