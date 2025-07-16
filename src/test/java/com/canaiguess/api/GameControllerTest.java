package com.canaiguess.api;

import com.canaiguess.api.config.JwtAuthenticationFilter;
import com.canaiguess.api.controller.GameController;
import com.canaiguess.api.dto.NewGameResponseDTO;
import com.canaiguess.api.enums.Role;
import com.canaiguess.api.model.User;
import com.canaiguess.api.service.GameService;
import com.canaiguess.api.service.ImageGameService;
import com.canaiguess.api.service.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private ImageGameService imageGameService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void createGame_asAuthenticatedUser_shouldSucceed() throws Exception {
        String token = "fake-jwt";
        String requestJson = """
            {
                "batchCount": 3,
                "batchSize": 5,
                "difficulty": 2
            }
        """;

        // Mock expected user and JWT behavior
        User mockUser = new User();
        mockUser.setId(42L);
        mockUser.setUsername("testuser");
        mockUser.setRole(Role.USER); // or whatever enum value applies

        Mockito.when(jwtService.extractUsername(token)).thenReturn("testuser");
        Mockito.when(jwtService.isTokenValid(Mockito.eq(token), Mockito.any())).thenReturn(true);
        Mockito.when(userDetailsService.loadUserByUsername("testuser")).thenReturn(mockUser);

        // Mock GameService behavior
        Mockito.when(gameService.createGame(Mockito.any(), Mockito.eq(42L)))
                .thenReturn(new NewGameResponseDTO(123L));

        mockMvc.perform(post("/api/game")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(123L));

    }
}
