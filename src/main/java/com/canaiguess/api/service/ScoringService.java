package com.canaiguess.api.service;

import com.canaiguess.api.exception.GameDataIncompleteException;
import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.ImageGame;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.ImageGameRepository;
import com.canaiguess.api.repository.UserRepository;
import com.canaiguess.api.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoringService {

    private final ImageGameRepository imageGameRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public ScoringService(ImageGameRepository imageGameRepository,
                          UserRepository userRepository,
                          GameRepository gameRepository) {
        this.imageGameRepository = imageGameRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public void updateUserPoints(Game game) {
        List<ImageGame> imageGames = imageGameRepository.findByGame(game);
        if (imageGames.isEmpty()) {
            throw new GameDataIncompleteException("No ImageGame entries found for game ID: " + game.getId());
        }
        User user = game.getUser();

        int correct = (int) imageGames.stream()
                .filter(ImageGame::isUserGuessedCorrectly)
                .count();

        double batchSizeEffect = 1 + (1 - Math.exp((double) -game.getBatchSize() / 5)); // 1 to 2 (softmax)
        double difficultyEffect = game.getDifficulty() / 100.0; // 0 to 1 (proportional)
        int score = (int) Math.round(correct * difficultyEffect * batchSizeEffect * 10);

        // Add to user's total score
        user.setScore(user.getScore() + score);
        userRepository.save(user);

        // Save points to the Game entity
        game.setScore(score);
        gameRepository.save(game);
    }
}
