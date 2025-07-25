package com.canaiguess.api.controller;

import com.canaiguess.api.dto.*;
import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.User;
import com.canaiguess.api.service.GameService;
import com.canaiguess.api.service.GameSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN') or @gameSecurity.isOwner(#gameId, authentication)")
    @GetMapping("/{gameId}")
    @Operation(summary = "Get full game info (metadata + results)")
    public ResponseEntity<GameDTO> getGameById(
            @PathVariable String gameId,
            @AuthenticationPrincipal User user
    ) {
        GameDTO game = gameService.getGameByPublicId(gameId, user);
        return ResponseEntity.ok(game);
    }

    @PreAuthorize("hasRole('ADMIN') or @gameSecurity.isOwner(#gameId, authentication)")
    @PostMapping("/{gameId}/batch")
    @Operation(summary = "Fetch next image batch")
    public ResponseEntity<ImageBatchResponseDTO> getNextBatch(
            @PathVariable String gameId,
            @AuthenticationPrincipal User user
    ) {
        List<ImageDTO> images = gameSessionService.getNextBatchForGame(gameId, user);
        return ResponseEntity.ok(new ImageBatchResponseDTO(images));
    }

    @PreAuthorize("hasRole('ADMIN') or @gameSecurity.isOwner(#gameId, authentication)")
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




}
