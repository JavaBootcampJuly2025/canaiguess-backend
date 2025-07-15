package com.canaiguess.api.service;

import com.canaiguess.api.dto.GameDTO;
import com.canaiguess.api.dto.GameRequestDTO;
import com.canaiguess.api.dto.GameResponseDTO;
import com.canaiguess.api.model.Game;
import com.canaiguess.api.repository.GameRepository;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public GameResponseDTO createGame(GameRequestDTO request, String userId) {
        Game game = new Game();
        game.setGameMode(request.getGameMode());
        game.setBatches(request.getBatches());
        game.setDifficulty(request.getDifficulty());
        game.setUserId(userId);

        Game saved = gameRepository.save(game);
        return new GameResponseDTO(saved.getId());
    }

    public GameDTO getGameById(String gameId) {
        return gameRepository.findById(gameId)
            .map(game -> GameDTO.builder()
                    .gameMode(game.getGameMode())
                    .batchesLeft(game.getBatches())
                    .difficulty(game.getDifficulty())
                    .build()
            )
            .orElse(null);
    }

}
