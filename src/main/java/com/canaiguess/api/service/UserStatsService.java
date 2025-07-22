package com.canaiguess.api.service;

import com.canaiguess.api.dto.GameDTO;
import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.GameRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserStatsService {

    private final GameRepository gameRepository;
    private final GameService gameService;

    public UserStatsService(GameRepository gameRepository,
                            GameService gameService) {
        this.gameRepository = gameRepository;
        this.gameService = gameService;
    }

    public List<GameDTO> getGamesByUser(User user) {
        List<Game> games = gameRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(0, 10));

        return games.stream()
            .map(game -> {
                return gameService.getGameByPublicId(game.getPublicId(), user);
            })
            .toList();
    }

    // that was the same thing
//    public List<GameSummaryDTO> getLastGames(long userId) {
//        return gameRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 10))
//                .stream()
//                .map(g -> new GameSummaryDTO(g.getPublicId(), g.getScore(), g.getCreatedAt()))
//                .toList();
//    }

}
