package com.canaiguess.api.service;

import com.canaiguess.api.dto.GameResultsDTO;
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

    public List<GameResultsDTO> getGameResults(User user) {
        List<Game> games = gameRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(0, 10));

        return games.stream()
            .map(game -> {
                try {
                    return gameService.getGameResults(game.getPublicId(), user);
                } catch (IllegalStateException e) {
                    // game not finished or score not ready, return partial result
                    return new GameResultsDTO(
                            game.getPublicId(),
                            null, null, null,
                            game.getScore(),
                            game.getCreatedAt()
                    );
                }
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
