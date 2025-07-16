package com.canaiguess.api.service;

import com.canaiguess.api.dto.GameInfoResponseDTO;
import com.canaiguess.api.dto.NewGameRequestDTO;
import com.canaiguess.api.dto.NewGameResponseDTO;
import com.canaiguess.api.model.Game;
import com.canaiguess.api.repository.GameRepository;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final ImageGameService imageGameService;

    public GameService(GameRepository gameRepository, ImageGameService imageGameService) {
        this.imageGameService = imageGameService;
        this.gameRepository = gameRepository;
    }

    public NewGameResponseDTO createGame(NewGameRequestDTO request, String userId) {
        Game game = new Game();
        game.setBatchCount(request.getBatchCount());
        game.setDifficulty(request.getDifficulty());
        game.setUserId(userId);

        Game saved = gameRepository.save(game);

        // Delegate image allocation
        imageGameService.allocateImagesForGame(saved);

        return new NewGameResponseDTO(saved.getId());
    }

    public GameInfoResponseDTO getGameById(String gameId) {
        return gameRepository.findById(gameId)
            .map(game -> GameInfoResponseDTO.builder()
                    .batchCount(game.getBatchCount())
                    .batchSize(game.getBatchSize())
                    .currentBatch(game.getCurrentBatch())
                    .difficulty(game.getDifficulty())
                    .build()
            )
            .orElse(null);
    }

}
