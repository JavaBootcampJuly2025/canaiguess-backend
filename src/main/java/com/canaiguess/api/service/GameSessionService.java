package com.canaiguess.api.service;

import com.canaiguess.api.dto.ImageDTO;
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

    public List<Boolean> validateGuesses(Long gameId, User user, List<Boolean> guesses) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (!game.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to game");
        }

        int batch = game.getCurrentBatch();

        List<ImageGame> imageGames = imageGameRepository.findByGameAndBatchNumber(game, batch);

        if (imageGames.size() != guesses.size()) {
            throw new RuntimeException("Mismatched guesses and images in batch");
        }

        List<Boolean> correct = new ArrayList<>();
        for (int i = 0; i < imageGames.size(); i++) {
            ImageGame ig = imageGames.get(i);
            Image image = ig.getImage();
            boolean userGuess = guesses.get(i);
            boolean isAI = image.isFake();

            // stats update
            image.setTotal(image.getTotal() + 1);
            if (userGuess == isAI) {
                image.setCorrect(image.getCorrect() + 1);
                ig.setUserGuessedCorrectly(true);
                correct.add(true);
            } else {
                ig.setUserGuessedCorrectly(false);
                correct.add(false);
            }

            imageRepository.save(image);
            imageGameRepository.save(ig);
        }

        game.setCurrentBatch(batch + 1);

        // if this was the final batch, finish and score
        if (batch == game.getBatchCount()) {
            scoringService.updateUserPoints(game);
            game.setFinished(true);
        }

        gameRepository.save(game);

        return correct;
    }

    public List<ImageDTO> getNextBatchForGame(long gameId, User user) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (!game.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to game");
        }

        if (game.isFinished()) {
            throw new RuntimeException("Game is already finished");
        }

        int currentBatch = game.getCurrentBatch();
        List<ImageGame> imageGames = imageGameRepository.findByGameAndBatchNumber(game, currentBatch);

        if (imageGames.isEmpty()) {
            throw new RuntimeException("No images found for current batch");
        }

        return imageGames.stream()
                .map(ig -> new ImageDTO(
                        ig.getImage().getId(),
                        ig.getImage().getFilename()
                ))
                .toList();
    }

}
