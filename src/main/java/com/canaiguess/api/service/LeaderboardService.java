package com.canaiguess.api.service;

import com.canaiguess.api.dto.UserDTO;
import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.GameRepository;
import com.canaiguess.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public List<UserDTO> getLeaderboard() {
        List<User> topUsers = userRepository.findTop10ByOrderByScoreDesc();

        return topUsers.stream()
            .map(user -> {
                int totalGames = gameRepository.countGamesByUsername(user.getUsername());
                return UserDTO.from(user, totalGames);
            })
            .toList();
    }
}
