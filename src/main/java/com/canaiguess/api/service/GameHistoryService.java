package com.canaiguess.api.service;

import com.canaiguess.api.dto.GameSummaryDTO;
import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.GameRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameHistoryService {

    private final GameRepository gameRepository;

    public GameHistoryService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<GameSummaryDTO> getLast10Games(long userId) {
        List<Game> games = gameRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 10));
        return games.stream()
                .map(g -> new GameSummaryDTO(g.getId(), g.getScore()))
                .collect(Collectors.toList());
    }
}
