package com.canaiguess.api.controller;

import com.canaiguess.api.annotation.CanAccessGame;
import com.canaiguess.api.dto.*;
import com.canaiguess.api.model.User;
import com.canaiguess.api.service.GameService;
import com.canaiguess.api.service.GameSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@Tag(name = "Game", description = "Handles game creation, retrieval, and image batch fetching")
public class GameController {

    private final GameService gameService;
    private final GameSessionService gameSessionService;

    public GameController(GameService gameService, GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
        this.gameService = gameService;
    }

    @PostMapping
    @Operation(summary = "Create a new game")
    public ResponseEntity<NewGameResponseDTO> createGame(
            @RequestBody NewGameRequestDTO request,
            @AuthenticationPrincipal User user
    ) {

        NewGameResponseDTO response = gameService.createGame(request, user);
        return ResponseEntity.ok(response);

    }

    @CanAccessGame
    @GetMapping("/{gameId}")
    @Operation(summary = "Get game details by ID")
    public ResponseEntity<GameInfoResponseDTO> getGameById(@PathVariable String gameId) {
        GameInfoResponseDTO game = gameService.getGameByPublicId(gameId);
        return ResponseEntity.ok(game);
//        if (game != null) {
//            return ResponseEntity.ok(game);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
    }

    @CanAccessGame
    @PostMapping("/{gameId}/batch")
    @Operation(summary = "Fetch next image batch")
    public ResponseEntity<ImageBatchResponseDTO> getNextBatch(
            @PathVariable String gameId,
            @AuthenticationPrincipal User user
    ) {
        List<ImageDTO> images = gameSessionService.getNextBatchForGame(gameId, user);
        return ResponseEntity.ok(new ImageBatchResponseDTO(images));
    }

    @CanAccessGame
    @PostMapping("/{gameId}/guess")
    @Operation(summary = "Submit guesses for the current batch")
    public ResponseEntity<GuessResultDTO> validateGuesses(
            @PathVariable String gameId,
            @RequestBody GuessRequestDTO guessRequest,
            @AuthenticationPrincipal User user
    ) {
        List<Boolean> results = gameSessionService.validateGuesses(gameId, user, guessRequest.getGuesses());
        return ResponseEntity.ok(new GuessResultDTO(results));
    }

    @CanAccessGame
    @PostMapping("/{gameId}/results")
    @Operation(
            summary = "Get results for a game",
            description = "Returns the number of correct and incorrect guesses for the game"
    )
    public ResponseEntity<GameResultsDTO> getGameResults(
            @PathVariable String gameId,
            @AuthenticationPrincipal User user
    ) {
        GameResultsDTO results = gameService.getGameResults(gameId, user);
        return ResponseEntity.ok(results);
    }

}
