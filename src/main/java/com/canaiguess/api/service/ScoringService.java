package com.canaiguess.api.service;

import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.ImageGame;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.ImageGameRepository;
import com.canaiguess.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoringService {

    private final ImageGameRepository imageGameRepository;
    private final UserRepository userRepository;

    public ScoringService(ImageGameRepository imageGameRepository,
                          UserRepository userRepository) {
        this.imageGameRepository = imageGameRepository;
        this.userRepository = userRepository;
    }

    public void updateUserPoints(Game game) {
        List<ImageGame> imageGames = imageGameRepository.findByGame(game);
        User user = game.getUser();

        int correct = (int) imageGames.stream()
                .filter(ImageGame::isUserGuessedCorrectly)
                .count();

        double batchSizeEffect = 1 + (1 - Math.exp((double) -game.getBatchSize() / 5)); // 1 to 2 (softmax)
        double difficultyEffect = game.getDifficulty() / 100.0; // 0 to 1 (proportional)
        int score = (int) Math.round(correct * difficultyEffect * batchSizeEffect * 10); // 0 to [batchSize * batchCount * 20]

        user.setScore(user.getScore() + score);
        game.setScore(score);

        userRepository.save(user);
    }
}
