package com.canaiguess.api.controller;

import com.canaiguess.api.config.MockUserArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;

import com.canaiguess.api.config.JwtAuthenticationFilter;
import com.canaiguess.api.dto.GameDTO;
import com.canaiguess.api.dto.UpdateUserRequestDTO;
import com.canaiguess.api.enums.Role;
import com.canaiguess.api.model.Game;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.GameRepository;
import com.canaiguess.api.repository.UserRepository;
import com.canaiguess.api.service.GameService;
import com.canaiguess.api.service.UserService;
import com.canaiguess.api.service.UserStatsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private GameRepository gameRepository;
    @MockBean
    private GameService gameService;
    @MockBean
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserStatsService userStatsService(GameRepository gameRepository, GameService gameService, UserRepository userRepository) {
            return new UserStatsService(gameRepository, gameService, userRepository);
        }

        @Bean
        public UserService userService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
            return new UserService(userRepository, passwordEncoder);
        }
    }

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setScore(100);
        testUser.setTotalGuesses(20);
        testUser.setCorrectGuesses(15);
        testUser.setRole(Role.USER);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(gameRepository.countGamesByUsername("testuser")).thenReturn(5);
    }

    @Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Test
    void getUserStats_returnsStats() throws Exception {
        mockMvc.perform(get("/api/user/testuser/stats")
                        .principal(() -> "testuser")) // In Spring, the Principal represents the currently authenticated user
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.accuracy").value(0.75))
                .andExpect(jsonPath("$.totalGames").value(5));
    }

    // Injects a mock User bean as @AuthenticationPrincipal
    @PostConstruct
    void init() {
        User actingUser = new User();
        actingUser.setUsername("testuser");
        actingUser.setRole(Role.USER);

        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
        resolvers.add(new MockUserArgumentResolver(actingUser));
        resolvers.addAll(requestMappingHandlerAdapter.getArgumentResolvers());
        requestMappingHandlerAdapter.setArgumentResolvers(resolvers);
    }

    @Test
    void getUserGames_returnsList() throws Exception {
        Game game = new Game();
        game.setId(1L);
        game.setPublicId("abc123");
        game.setUser(testUser);
        game.setCreatedAt(LocalDateTime.now());

        GameDTO gameDTO = GameDTO.builder()
                .id("abc123")
                .accuracy(0.8)
                .score(200)
                .createdAt(game.getCreatedAt())
                .correct(8)
                .total(10)
                .batchCount(1)
                .batchSize(10)
                .currentBatch(1)
                .difficulty(2)
                .finished(true)
                .build();

        when(gameRepository.findByUserIdOrderByCreatedAtDesc(eq(1L), any()))
                .thenReturn(List.of(game));

        when(gameService.getGameByPublicId("abc123", testUser))
                .thenReturn(gameDTO);

        mockMvc.perform(get("/api/user/testuser/games")
                        .principal(() -> "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value("abc123"))
                .andExpect(jsonPath("$[0].score").value(200));
    }

    @Test
    void updateUser_updatesPassword() throws Exception {
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setCurrentPassword("password");
        dto.setNewPassword("newPassword123");

        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");

        mockMvc.perform(patch("/api/user/testuser/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .principal(() -> "testuser"))
                .andExpect(status().isNoContent());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("newEncodedPassword", captor.getValue().getPassword());
    }

    @Test
    void deleteUser_deletesUser() throws Exception {
        mockMvc.perform(delete("/api/user/testuser/delete")
                        .principal(() -> "testuser"))
                .andExpect(status().isNoContent());

        verify(userRepository).delete(testUser);
    }

    @Test
    void updateUser_unauthorizedUser_returnsForbidden() throws Exception {
        User anotherUser = new User();
        anotherUser.setUsername("otheruser");

        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setCurrentPassword("password");
        dto.setNewPassword("hacker");

        // Do not match usernames
        mockMvc.perform(patch("/api/user/testuser/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .principal(() -> "otheruser"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateUser_wrongCurrentPassword_returnsBadRequest() throws Exception {
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setCurrentPassword("wrong");
        dto.setNewPassword("irrelevant");

        when(passwordEncoder.matches("wrong", "encodedPassword")).thenReturn(false);

        mockMvc.perform(patch("/api/user/testuser/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .principal(() -> "testuser"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateUser_emailChanged_success() throws Exception {
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setCurrentPassword("password");
        dto.setEmail("newemail@example.com");

        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(userRepository.findByEmail("newemail@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/user/testuser/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .principal(() -> "testuser"))
                .andExpect(status().isNoContent());

        verify(userRepository).save(argThat(user -> "newemail@example.com".equals(user.getEmail())));
    }

    @Test
    void updateUser_emailAlreadyExists_returnsConflict() throws Exception {
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setCurrentPassword("password");
        dto.setEmail("taken@example.com");

        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(new User()));

        mockMvc.perform(patch("/api/user/testuser/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .principal(() -> "testuser"))
                .andExpect(status().isConflict());
    }

    @Test
    void adminCanUpdateOtherUser() throws Exception {
        UpdateUserRequestDTO dto = new UpdateUserRequestDTO();
        dto.setCurrentPassword("adminpass");
        dto.setNewPassword("updated");

        User admin = new User();
        admin.setUsername("admin");
        admin.setRole(Role.ADMIN);

        when(passwordEncoder.matches("adminpass", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("updated")).thenReturn("encodedNew");

        mockMvc.perform(patch("/api/user/testuser/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .principal(() -> "admin"))
                .andExpect(status().isNoContent());

        verify(userRepository).save(argThat(user -> "encodedNew".equals(user.getPassword())));
    }

    @Test
    void adminCanDeleteOtherUser() throws Exception {
        User admin = new User();
        admin.setUsername("admin");
        admin.setRole(Role.ADMIN);

        mockMvc.perform(delete("/api/user/testuser/delete")
                        .principal(() -> "admin"))
                .andExpect(status().isNoContent());

        verify(userRepository).delete(testUser);
    }
}
