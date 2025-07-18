package com.canaiguess.api.controller;

import com.canaiguess.api.dto.GameSummaryDTO;
import com.canaiguess.api.service.GameHistoryService;
import org.springframework.web.bind.annotation.*;
import com.canaiguess.api.model.User;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class GameHistoryController {

    private final GameHistoryService gameHistoryService;

    public GameHistoryController(GameHistoryService gameHistoryService) {
        this.gameHistoryService = gameHistoryService;
    }

    @GetMapping("/last10/{userId}")
    public List<GameSummaryDTO> getLast10Games(@PathVariable User user) {
        return gameHistoryService.getLast10Games(user);
    }
}
