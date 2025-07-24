package com.canaiguess.api.service;

import com.canaiguess.api.dto.GameDTO;
import com.canaiguess.api.dto.UserDTO;
import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.GameRepository;
import com.canaiguess.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserStatsService {

    private final GameRepository gameRepository;
    private final GameService gameService;
    private final UserRepository userRepository;

    public UserDTO getUserStats(String username)
    {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        int totalGames = gameRepository.countGamesByUsername(username);
        return UserDTO.from(user, totalGames);

    }

    public List<GameDTO> getGamesByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        List<Game> games = gameRepository.findByUserIdOrderByCreatedAtDesc(
                user.getId(),
                PageRequest.of(0, 10)
        );

        return games.stream()
                .map(game -> gameService.getGameByPublicId(game.getPublicId(), user))
                .toList();
    }
}
