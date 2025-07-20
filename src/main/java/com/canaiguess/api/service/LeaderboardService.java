package com.canaiguess.api.service;

import com.canaiguess.api.dto.LeaderboardDTO;
import com.canaiguess.api.model.ImageGame;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.ImageGameRepository;
import com.canaiguess.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardService {

    private final UserRepository userRepository;
    private final ImageGameRepository imageGameRepository;

    public LeaderboardService(UserRepository userRepository, ImageGameRepository imageGameRepository) {
        this.userRepository = userRepository;
        this.imageGameRepository = imageGameRepository;
    }

    // ✅ 1) Points leaderboard
    public List<LeaderboardDTO> getLeaderboard() {
        List<User> topUsers = userRepository.findTop10ByOrderByScoreDesc();
        return topUsers.stream()
                .map(user -> new LeaderboardDTO(user.getUsername(), user.getScore(), null)) // accuracy null
                .toList();
    }

    // ✅ 2) Accuracy leaderboard
    public List<LeaderboardDTO> getAccuracyLeaderboard() {
        List<User> allUsers = userRepository.findAll();

        return allUsers.stream()
                .map(user -> {
                    List<ImageGame> userImageGames = imageGameRepository.findByGameUserId(user.getId());
                    long total = userImageGames.size();
                    long correct = userImageGames.stream()
                            .filter(ImageGame::isUserGuessedCorrectly)
                            .count();
                    double accuracy = total > 0 ? ((double) correct / total) * 100.0 : 0.0;

                    return new LeaderboardDTO(user.getUsername(), 0, accuracy); // score zero
                })
                .filter(dto -> dto.getAccuracy() > 0)
                .sorted((u1, u2) -> Double.compare(u2.getAccuracy(), u1.getAccuracy()))
                .limit(10)
                .toList();
    }
}
