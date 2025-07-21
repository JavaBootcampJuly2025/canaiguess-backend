package com.canaiguess.api.controller;

import com.canaiguess.api.dto.LastGameDTO;
import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
public class UserStatsController {

    @Autowired
    private GameRepository gameRepository;

    @GetMapping("/last-games/{userId}")
    public List<LastGameDTO> getLastGames(@AuthenticationPrincipal User user) {
        List<Game> games = gameRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(0, 10));
        return games.stream()
                .map(game -> new LastGameDTO(game.getPublicId(), game.getScore(), game.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
