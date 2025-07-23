package com.canaiguess.api.controller;

import com.canaiguess.api.dto.GameDTO;
import com.canaiguess.api.model.User;
import com.canaiguess.api.service.UserStatsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserStatsController {

    private final UserStatsService userStatsService;

    public UserStatsController(UserStatsService userStatsService) {
        this.userStatsService = userStatsService;
    }

    @GetMapping("/{username}/games")
    public List<GameDTO> getUserGames(@PathVariable String username,
                                      @AuthenticationPrincipal User user) {
        return userStatsService.getGamesByUser(username);
    }
}
