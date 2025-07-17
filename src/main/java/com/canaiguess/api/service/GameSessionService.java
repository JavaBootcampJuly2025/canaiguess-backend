package com.canaiguess.api.service;

import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.Image;
import com.canaiguess.api.model.ImageGame;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.GameRepository;
import com.canaiguess.api.repository.ImageGameRepository;
import com.canaiguess.api.repository.ImageRepository;
import com.canaiguess.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameSessionService {

    private final ScoringService scoringService;
    private final ImageRepository imageRepository;
    private final ImageGameRepository imageGameRepository;
    private final GameRepository gameRepository;

    public GameSessionService(ImageRepository imageRepository,
                            ImageGameRepository imageGameRepository,
                            GameRepository gameRepository,
                            ScoringService scoringService) {

        this.imageRepository = imageRepository;
        this.imageGameRepository = imageGameRepository;
        this.gameRepository = gameRepository;
        this.scoringService = scoringService;
    }

    public List<Boolean> validateGuesses(List<String> imageUrls, List<Boolean> guesses) {
        if (imageUrls.size() != guesses.size()) {
            throw new IllegalArgumentException("Mismatched image and guess count");
        }

        List<Boolean> correct = new ArrayList<>();

        for (int i = 0; i < imageUrls.size(); i++) {
            String url = imageUrls.get(i);
            boolean userGuess = guesses.get(i);

            Image image = imageRepository.findByFilename(url)
                    .orElseThrow(() -> new RuntimeException("Image not found"));

            boolean isAI = image.isFake();

            // update statistics
            image.setTotal(image.getTotal() + 1);
            if (userGuess == isAI) {
                image.setCorrect(image.getCorrect() + 1);
                correct.add(true);
            } else {
                correct.add(false);
            }

            imageRepository.save(image);
        }

        return correct;
    }

    public List<String> getNextBatchForGame(long gameId, long userId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (game.getUserId() != userId) {
            throw new RuntimeException("Unauthorized access to game");
        }

        int currentBatch = game.getCurrentBatch();

        if (currentBatch > game.getBatchCount()) {

            // update user points only and end the game
            scoringService.updateUserPoints(game);

            game.setFinished(true);
            gameRepository.save(game);

            return List.of(); // empty batch <=> game finished
        }

        List<ImageGame> imageGames = imageGameRepository.findByGameAndBatchNumber(game, currentBatch);
        if (imageGames.isEmpty()) {
            throw new RuntimeException("No images found for current batch");
        }

        game.setCurrentBatch(currentBatch + 1);
        gameRepository.save(game);

        return imageGames.stream()
                .map(ig -> ig.getImage().getFilename())
                .toList();
    }
}
