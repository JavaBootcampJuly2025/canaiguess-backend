package com.canaiguess.api.controller;

import com.canaiguess.api.dto.GlobalStatsDTO;
import com.canaiguess.api.service.GlobalStatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/global")
public class GlobalStatsController
{

    private final GlobalStatsService globalStatsService;

    public GlobalStatsController(GlobalStatsService globalStatsService)
    {
        this.globalStatsService = globalStatsService;
    }

    @GetMapping("/stats")
    public GlobalStatsDTO getGlobalStats()
    {
        return globalStatsService.getGlobalStats();
    }
}
