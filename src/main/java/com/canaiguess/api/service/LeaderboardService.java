package com.canaiguess.api.service;

import com.canaiguess.api.dto.LeaderboardDTO;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardService
{

    private final UserRepository userRepository;

    public LeaderboardService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public List<LeaderboardDTO> getLeaderboard()
    {
        List<User> topUsers = userRepository.findTop10ByOrderByScoreDesc();
        return topUsers.stream()
                .map(user -> new LeaderboardDTO(user.getUsername(), user.getScore()))
                .collect(Collectors.toList());
    }
}
