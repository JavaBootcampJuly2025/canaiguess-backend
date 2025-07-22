package com.canaiguess.api.controller;

import com.canaiguess.api.dto.UserDTO;
import com.canaiguess.api.service.LeaderboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController
{

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService)
    {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping
    public List<UserDTO> getLeaderboard()
    {
        return leaderboardService.getLeaderboard();
    }

}
