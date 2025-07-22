package com.canaiguess.api.service;

import com.canaiguess.api.dto.UserDTO;
import com.canaiguess.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardService {

    private final UserRepository userRepository;

    public LeaderboardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getLeaderboard() {
        return userRepository.findAll().stream()
            .map(user -> {
                double accuracy = user.getTotalGuesses() > 0
                    ? ((double) user.getCorrectGuesses() / user.getTotalGuesses()) * 100.0
                    : 0.0;
                return new UserDTO(user.getUsername(), user.getScore(), accuracy);
            })
            .sorted((u1, u2) -> Integer.compare(u2.getScore(), u1.getScore()))
            .limit(10)
            .toList();
    }
}
