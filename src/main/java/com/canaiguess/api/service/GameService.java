package com.canaiguess.api.service;

import com.canaiguess.api.dto.GameDTO;

import com.canaiguess.api.dto.NewGameRequestDTO;
import com.canaiguess.api.dto.NewGameResponseDTO;
import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.GameRepository;
import com.canaiguess.api.repository.ImageGameRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final ImageAllocatorService imageAllocatorService;

    public GameService(GameRepository gameRepository,
                       ImageAllocatorService imageAllocatorService) {
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

    public GameDTO getGameByPublicId(String gameId, User user) {
        Game game = gameRepository.findByPublicId(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (game.getUser() != null && !game.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to game details");
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
