package com.canaiguess.api.service;

import com.canaiguess.api.dto.GameDTO;

import com.canaiguess.api.dto.NewGameRequestDTO;
import com.canaiguess.api.dto.NewGameResponseDTO;
import com.canaiguess.api.exception.GameDataIncompleteException;
import com.canaiguess.api.exception.UnauthorizedAccessException;
import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final ImageAllocatorService imageAllocatorService;

    public NewGameResponseDTO createGame(NewGameRequestDTO request, User user) {

        if (request.getBatchCount() <= 0 || request.getBatchSize() <= 0 || request.getDifficulty() < 0) {
            throw new GameDataIncompleteException("Invalid game configuration values"
                    + ". Batch count: " + request.getBatchCount()
                    + ". Batch size: " + request.getBatchSize()
                    + ". Difficulty: " + request.getDifficulty()
            );
        }
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

    public GameDTO getGameByPublicId(String gameId, User user) {
        Game game = gameRepository.findByPublicId(gameId)
                .orElseThrow(() -> new GameDataIncompleteException("Game not found by gameId: " + gameId));

        if (game.getUser() != null && !game.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("Unauthorized access to game details");
        }

        return GameDTO.builder()
                .id(game.getPublicId())
                .correct(game.getCorrectGuesses())
                .total(game.getTotalGuesses())
                .accuracy(game.getAccuracy())
                .score(game.getScore())
                .createdAt(game.getCreatedAt())
                .currentBatch(game.getCurrentBatch())
                .batchCount(game.getBatchCount())
                .batchSize(game.getBatchSize())
                .difficulty(game.getDifficulty())
                .finished(game.isFinished())
                .build();
    }


}
