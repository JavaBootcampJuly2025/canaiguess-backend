package com.canaiguess.api.service;

import com.canaiguess.api.dto.ImageDTO;
import com.canaiguess.api.exception.GameDataIncompleteException;
import com.canaiguess.api.exception.UnauthorizedAccessException;
import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.Image;
import com.canaiguess.api.model.ImageGame;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.GameRepository;
import com.canaiguess.api.repository.ImageGameRepository;
import com.canaiguess.api.repository.ImageRepository;
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

    public List<Boolean> validateGuesses(String gameId, User user, List<Boolean> guesses) {
        Game game = gameRepository.findByPublicId(gameId)
                .orElseThrow(() -> new GameDataIncompleteException("Game not found by gameId: " + gameId));

        if (!game.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("Unauthorized access to the game");
        }

        int batch = game.getCurrentBatch();
        List<ImageGame> imageGames = imageGameRepository.findByGameAndBatchNumber(game, batch);

        if (guesses == null || guesses.isEmpty()) {
            throw new GameDataIncompleteException("No guesses provided for validation.");
        }
        if (imageGames.size() != guesses.size()) {
            throw new GameDataIncompleteException("Mismatched guesses and images in batch");
        }

        List<Boolean> correct = new ArrayList<>();
        int correctCount = 0;

        for (int i = 0; i < imageGames.size(); i++) {
            ImageGame ig = imageGames.get(i);
            Image image = ig.getImage();
            boolean userGuess = guesses.get(i);
            boolean isAI = image.isFake();

            image.setTotal(image.getTotal() + 1);

            if (userGuess == isAI) {
                image.setCorrect(image.getCorrect() + 1);
                ig.setUserGuessedCorrectly(true);
                correct.add(true);
                correctCount++;
            } else {
                ig.setUserGuessedCorrectly(false);
                correct.add(false);
            }

            imageRepository.save(image);
            imageGameRepository.save(ig);
        }

        // Update derived stats on game
        game.setCorrectGuesses(game.getCorrectGuesses() + correctCount);
        game.setTotalGuesses(game.getTotalGuesses() + guesses.size());
        game.setCurrentBatch(batch + 1);
        gameRepository.save(game);

        // score game after final batch
        if (batch == game.getBatchCount()) {
            scoringService.updateScore(game);
        }

        return correct;
    }


    public List<ImageDTO> getNextBatchForGame(String gameId, User user) {
        Game game = gameRepository.findByPublicId(gameId)
                .orElseThrow(() -> new GameDataIncompleteException("Game not found by publicId: " + gameId));

        if (!game.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("Unauthorized access to the game");
        }

        if (game.isFinished()) {
            throw new GameDataIncompleteException("Game is already finished!");
        }

        int currentBatch = game.getCurrentBatch();
        List<ImageGame> imageGames = imageGameRepository.findByGameAndBatchNumber(game, currentBatch);

        if (imageGames.isEmpty()) {
            throw new GameDataIncompleteException("No images found for current batch");
        }

        return imageGames.stream()
                .map(ig -> new ImageDTO(
                        ig.getImage().getPublicId(),
                        ig.getImage().getUrl()
                ))
                .toList();
    }

}
