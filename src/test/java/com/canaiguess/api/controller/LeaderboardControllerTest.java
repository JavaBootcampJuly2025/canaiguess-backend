package com.canaiguess.api.controller;

import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.GameRepository;
import com.canaiguess.api.repository.UserRepository;
import com.canaiguess.api.service.LeaderboardService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LeaderboardController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = com.canaiguess.api.config.JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
@Import(LeaderboardControllerTest.LeaderboardServiceTestConfig.class)
public class LeaderboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GameRepository gameRepository;

    @Autowired
    private LeaderboardService leaderboardService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .username("john_doe")
                .email("john@example.com")
                .score(150)
                .totalGuesses(100)
                .correctGuesses(75)
                .role(com.canaiguess.api.enums.Role.USER)
                .build();
    }

    @Test
    void shouldReturnLeaderboard() throws Exception {
        when(userRepository.findTop10ByOrderByScoreDesc())
                .thenReturn(List.of(sampleUser));

        when(gameRepository.countGamesByUsername("john_doe"))
                .thenReturn(5);

        mockMvc.perform(get("/api/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("john_doe")))
                .andExpect(jsonPath("$[0].score", is(150)))
                .andExpect(jsonPath("$[0].accuracy", is(0.75)))
                .andExpect(jsonPath("$[0].totalGuesses", is(100)))
                .andExpect(jsonPath("$[0].correctGuesses", is(75)))
                .andExpect(jsonPath("$[0].totalGames", is(5)));
    }

    @Test
    void shouldReturnEmptyLeaderboardWhenNoUsers() throws Exception {
        when(userRepository.findTop10ByOrderByScoreDesc())
                .thenReturn(List.of()); // empty list

        mockMvc.perform(get("/api/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnLeaderboardWithMultipleUsers() throws Exception {
        User user1 = User.builder()
                .username("alice")
                .email("alice@example.com")
                .score(200)
                .totalGuesses(50)
                .correctGuesses(40)
                .role(com.canaiguess.api.enums.Role.USER)
                .build();

        User user2 = User.builder()
                .username("bob")
                .email("bob@example.com")
                .score(150)
                .totalGuesses(100)
                .correctGuesses(50)
                .role(com.canaiguess.api.enums.Role.USER)
                .build();

        when(userRepository.findTop10ByOrderByScoreDesc())
                .thenReturn(List.of(user1, user2));

        when(gameRepository.countGamesByUsername("alice")).thenReturn(3);
        when(gameRepository.countGamesByUsername("bob")).thenReturn(7);

        mockMvc.perform(get("/api/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is("alice")))
                .andExpect(jsonPath("$[0].score", is(200)))
                .andExpect(jsonPath("$[0].accuracy", is(0.8)))
                .andExpect(jsonPath("$[0].totalGames", is(3)))

                .andExpect(jsonPath("$[1].username", is("bob")))
                .andExpect(jsonPath("$[1].score", is(150)))
                .andExpect(jsonPath("$[1].accuracy", is(0.5)))
                .andExpect(jsonPath("$[1].totalGames", is(7)));
    }

    @Test
    void shouldHandleUserWithZeroTotalGuesses() throws Exception {
        User user = User.builder()
                .username("zero_user")
                .email("zero@example.com")
                .score(100)
                .totalGuesses(0)
                .correctGuesses(0)
                .role(com.canaiguess.api.enums.Role.USER)
                .build();

        when(userRepository.findTop10ByOrderByScoreDesc())
                .thenReturn(List.of(user));

        when(gameRepository.countGamesByUsername("zero_user"))
                .thenReturn(2);

        mockMvc.perform(get("/api/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("zero_user")))
                .andExpect(jsonPath("$[0].score", is(100)))
                .andExpect(jsonPath("$[0].accuracy", is(0.0)))
                .andExpect(jsonPath("$[0].totalGuesses", is(0)))
                .andExpect(jsonPath("$[0].correctGuesses", is(0)))
                .andExpect(jsonPath("$[0].totalGames", is(2)));
    }

    @TestConfiguration
    static class LeaderboardServiceTestConfig {
        @Bean
        public LeaderboardService leaderboardService(UserRepository userRepository, GameRepository gameRepository) {
            return new LeaderboardService(userRepository, gameRepository);
        }
    }
}
