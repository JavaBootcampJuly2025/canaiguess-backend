package com.canaiguess.api.controller;

import com.canaiguess.api.dto.GameInfoResponseDTO;
import com.canaiguess.api.dto.NewGameRequestDTO;
import com.canaiguess.api.dto.NewGameResponseDTO;
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
    public ResponseEntity<NewGameResponseDTO> createGame(
            @RequestBody NewGameRequestDTO request
            // @AuthenticationPrincipal User user // TODO
    ) {

        // String userId = (user != null) ? user.getId() : null;
        NewGameResponseDTO response = gameService.createGame(request, null);
        return ResponseEntity.ok(response);

    }

    // TODO: check authorization as only logged in users may resume a game
    @GetMapping("/{gameId}")
<<<<<<< HEAD
    public ResponseEntity<GameDTO> getGameById(@PathVariable String gameId) {
        GameDTO game = gameService.getGameById(gameId);
=======
    public ResponseEntity<GameInfoResponseDTO> getGameById(@PathVariable String gameId) {
        GameInfoResponseDTO game = gameService.getGameById(gameId);
>>>>>>> cd0b1f7426cbe376b04b68191f4a3b3e2dd36553
        if (game != null) {
            return ResponseEntity.ok(game);

        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
