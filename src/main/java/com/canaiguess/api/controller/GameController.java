package com.canaiguess.api.controller;

import com.canaiguess.api.dto.GameInfoResponseDTO;
import com.canaiguess.api.dto.ImageBatchResponseDTO;
import com.canaiguess.api.dto.NewGameRequestDTO;
import com.canaiguess.api.dto.NewGameResponseDTO;
import com.canaiguess.api.model.User;
import com.canaiguess.api.service.GameService;
import com.canaiguess.api.service.ImageGameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

// @AuthenticationPrincipal possible
@RestController
@RequestMapping("/api/game")
@Tag(name = "Game", description = "Handles game creation, retrieval, and image batch fetching")
public class GameController {

    private final GameService gameService;
    private final ImageGameService imageGameService;
    public GameController(GameService gameService, ImageGameService imageGameService) {
        this.imageGameService = imageGameService;
        this.gameService = gameService;
    }

    @PostMapping
    @Operation(
            summary = "Create a new game",
            description = "Creates a new game for the authenticated user"
    )
    public ResponseEntity<NewGameResponseDTO> createGame(
            @RequestBody NewGameRequestDTO request,
            @AuthenticationPrincipal User user
    ) {

        long userId = (user != null) ? user.getId() : null;
        NewGameResponseDTO response = gameService.createGame(request, userId);
        return ResponseEntity.ok(response);

    }

    // TODO: check authorization as only logged in users may resume a game
    @GetMapping("/{gameId}")
    @Operation(
            summary = "Get game by ID",
            description = "Returns game details by ID if it exists"
    )
    public ResponseEntity<GameInfoResponseDTO> getGameById(@PathVariable Long gameId) {
        GameInfoResponseDTO game = gameService.getGameById(gameId);
        if (game != null) {
            return ResponseEntity.ok(game);

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/game/{gameId}/batch")
    @Operation(
            summary = "Fetch next image batch",
            description = "Returns the next set of image URLs for a game"
    )
    public ResponseEntity<ImageBatchResponseDTO> getNextBatch(
            @PathVariable Long gameId,
            @AuthenticationPrincipal User user
    ) {
        List<String> imageUrls = imageGameService.getNextBatchForGame(gameId, user.getId());
        return ResponseEntity.ok(new ImageBatchResponseDTO(imageUrls));
    }


}
