package com.canaiguess.api.service;

import com.canaiguess.api.dto.UserDTO;
import com.canaiguess.api.dto.GameDTO;
import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.GameRepository;
import com.canaiguess.api.repository.ImageGameRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserStatsService
{

    private final GameRepository gameRepository;
    private final ImageGameRepository imageGameRepository;
    private final GameService gameService;

    public UserStatsService(GameRepository gameRepository, GameService gameService, ImageGameRepository imageGameRepository)
    {
        this.gameRepository = gameRepository;
        this.gameService = gameService;
        this.imageGameRepository = imageGameRepository;
    }

    // Main user stats
    public UserDTO getUserStats(User user)
    {
        int totalGames = user.getGames() != null ? user.getGames().size() : 0;

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

    // Last 10 games
    public List<GameDTO> getGamesByUser(User user)
    {
        List<Game> games = gameRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(0, 10));
        return games.stream()
                .map(game -> gameService.getGameByPublicId(game.getPublicId(), user))
                .toList();
    }
}
