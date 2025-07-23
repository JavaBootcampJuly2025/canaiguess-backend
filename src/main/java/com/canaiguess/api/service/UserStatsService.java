package com.canaiguess.api.service;

import com.canaiguess.api.dto.GameDTO;
import com.canaiguess.api.dto.UserDTO;
import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.GameRepository;
import com.canaiguess.api.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserStatsService {

    private final GameRepository gameRepository;
    private final GameService gameService;
    private final UserRepository userRepository;

    public UserStatsService(GameRepository gameRepository,
                            GameService gameService,
                            UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.gameService = gameService;
        this.userRepository = userRepository;
    }

    public UserDTO getUserStats(String username)
    {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        int totalGames = gameRepository.countGamesByUsername(username);

        double avgAccuracy = 0.0;
        if (user.getTotalGuesses() > 0)
        {
            avgAccuracy = (double) user.getCorrectGuesses() / user.getTotalGuesses();
        }

        return UserDTO.builder()
                .username(user.getUsername())
                .score(user.getScore())
                .accuracy(avgAccuracy)
                .totalGuesses(user.getTotalGuesses())
                .correctGuesses(user.getCorrectGuesses())
                .totalGames(totalGames)
                .build();
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
