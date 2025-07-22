package com.canaiguess.api.service;

import com.canaiguess.api.dto.GameInfoResponseDTO;
import com.canaiguess.api.dto.GameResultsDTO;
import com.canaiguess.api.dto.NewGameRequestDTO;
import com.canaiguess.api.dto.NewGameResponseDTO;
import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.ImageGame;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.GameRepository;
import com.canaiguess.api.repository.ImageGameRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final ImageGameRepository imageGameRepository;
    private final ImageAllocatorService imageAllocatorService;

    public GameService(GameRepository gameRepository,
                       ImageAllocatorService imageAllocatorService,
                       ImageGameRepository imageGameRepository) {
        this.imageGameRepository = imageGameRepository;
        this.imageAllocatorService = imageAllocatorService;
        this.gameRepository = gameRepository;
    }

    public NewGameResponseDTO createGame(NewGameRequestDTO request, User user) {
        Game game = new Game();

        game.setBatchCount(request.getBatchCount());
        game.setDifficulty(request.getDifficulty());
        game.setBatchSize(request.getBatchSize());
        game.setCurrentBatch(1);

        // null for unauthorized games
        game.setUser(user);

        // ensure a unique public id is generated
        String publicId;
        do {
            publicId = RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        } while (gameRepository.existsByPublicId(publicId));

        game.setPublicId(publicId);

        // populate with id and timestamp fields
        Game saved = gameRepository.save(game);

        // delegate image allocation
        imageAllocatorService.allocateImagesForGame(saved);

        return new NewGameResponseDTO(saved.getPublicId());
    }

    public GameInfoResponseDTO getGameByPublicId(String gameId) {
        return gameRepository.findByPublicId(gameId)
                .map(game -> GameInfoResponseDTO.builder()
                        .batchCount(game.getBatchCount())
                        .batchSize(game.getBatchSize())
                        .currentBatch(game.getCurrentBatch())
                        .difficulty(game.getDifficulty())
                        .build())
                .orElse(null);
    }

    public GameResultsDTO getGameResults(String gameId, User user) {
        Game game = gameRepository.findByPublicId(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (!game.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to game results");
        }

        if (game.getCurrentBatch() <= game.getBatchCount()) {
            throw new IllegalStateException("Game is not finished yet.");
        }

        if (game.getScore() == null) {
            throw new IllegalStateException("Results are not ready yet.");
        }

        List<ImageGame> imageGames = imageGameRepository.findByGame(game);
        int correct = (int) imageGames.stream().filter(ImageGame::isUserGuessedCorrectly).count();
        int total = imageGames.size();
        int incorrect = total - correct;
        double accuracy = total > 0 ? (double) correct / total : 0.0;

        return new GameResultsDTO(
                game.getPublicId(),
                correct,
                incorrect,
                accuracy,
                game.getScore(),
                game.getCreatedAt()
        );
    }

}
