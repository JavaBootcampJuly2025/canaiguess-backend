package com.canaiguess.api.service;

import com.canaiguess.api.dto.UserDTO;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardService
{

    private final UserRepository userRepository;

    public LeaderboardService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getLeaderboard()
    {
        List<User> topUsers = userRepository.findTop10ByOrderByScoreDesc();

        return topUsers.stream()
            .map(user -> {
                double accuracy = 0.0;
                if (user.getTotalGuesses() > 0)
                {
                    accuracy = (double) user.getCorrectGuesses() / user.getTotalGuesses();
                }
                return new UserDTO(
                        user.getUsername(),
                        user.getScore(),
                        accuracy,
                        user.getTotalGuesses(),
                        user.getCorrectGuesses(),
                        user.getGames() != null ? user.getGames().size() : 0
                );
            })
            .toList();
    }
}
