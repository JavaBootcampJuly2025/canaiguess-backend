package com.canaiguess.api.controller;

import com.canaiguess.api.dto.GameDTO;
import com.canaiguess.api.dto.UserDTO;
import com.canaiguess.api.model.User;
import com.canaiguess.api.service.UserStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "Endpoints for retrieving user statistics and played games")
public class UserStatsController {

    private final UserStatsService userStatsService;

    public UserStatsController(UserStatsService userStatsService) {
        this.userStatsService = userStatsService;
    }

    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    @GetMapping("/{username}/stats")
    @Operation(summary = "Get user stats", description = "Returns stats for a specific user")
    public UserDTO getUserStats(@PathVariable String username,
                                @AuthenticationPrincipal User user) {
        return userStatsService.getUserStats(username);
    }

    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    @GetMapping("/{username}/games")
    @Operation(summary = "Get user games", description = "Returns the list of games played by a specific user")
    public List<GameDTO> getUserGames(@PathVariable String username,
                                      @AuthenticationPrincipal User user) {
        return userStatsService.getGamesByUser(username);
    }
}
