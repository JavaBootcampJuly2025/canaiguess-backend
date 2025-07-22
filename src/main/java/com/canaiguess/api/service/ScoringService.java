package com.canaiguess.api.service;

import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.ImageGame;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.ImageGameRepository;
import com.canaiguess.api.repository.UserRepository;
import com.canaiguess.api.repository.GameRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoringService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public ScoringService(UserRepository userRepository, GameRepository gameRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public void updateScore(Game game) {
        User user = game.getUser();
        int correct = game.getCorrectGuesses();

        // score calculation
        double batchSizeEffect = 1 + (1 - Math.exp((double) -game.getBatchSize() / 5));
        double difficultyEffect = game.getDifficulty() / 100.0;
        int score = (int) Math.round(correct * difficultyEffect * batchSizeEffect * 10);

        // save score to Game
        game.setScore(score);
        gameRepository.save(game);

        // update score for User
        int userScore = user.getScore() != null ? user.getScore() : 0;
        int userTotal = user.getTotalGuesses() != null ? user.getTotalGuesses() : 0;
        int userCorrect = user.getCorrectGuesses() != null ? user.getCorrectGuesses() : 0;

        user.setScore(userScore + score);
        user.setTotalGuesses(userTotal + (game.getTotalGuesses() != null ? game.getTotalGuesses() : 0));
        user.setCorrectGuesses(userCorrect + correct);

        userRepository.save(user);
    }
}

