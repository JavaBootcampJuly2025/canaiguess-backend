package com.canaiguess.api.service;

import com.canaiguess.api.dto.AccuracyLeaderboardDTO;
import com.canaiguess.api.dto.LeaderboardDTO;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardService
{

    private final UserRepository userRepository;

    public LeaderboardService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public List<LeaderboardDTO> getLeaderboard()
    {
        List<User> topUsers = userRepository.findTop10ByOrderByScoreDesc();
        return topUsers.stream()
                .map(user -> new LeaderboardDTO(user.getUsername(), user.getScore()))
                .collect(Collectors.toList());
    }

    public List<AccuracyLeaderboardDTO> getAccuracyLeaderboard() {
    List<User> allUsers = userRepository.findAll();

    return allUsers.stream()
        .filter(user -> user.getTotalGuesses() > 0) // skip people with no games
        .map(user -> {
            double accuracy = (double) user.getCorrectGuesses() / user.getTotalGuesses() * 100.0;
            return new AccuracyLeaderboardDTO(user.getUsername(), accuracy);
        })
        .sorted((u1, u2) -> Double.compare(u2.getAccuracy(), u1.getAccuracy())) // highest first
        .limit(10)
        .toList();
    }
}
