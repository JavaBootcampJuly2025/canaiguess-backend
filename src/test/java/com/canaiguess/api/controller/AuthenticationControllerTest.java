package com.canaiguess.api.controller;

import com.canaiguess.api.config.JwtAuthenticationFilter;
import com.canaiguess.api.dto.AuthenticationRequest;
import com.canaiguess.api.dto.AuthenticationResponse;
import com.canaiguess.api.dto.RegisterRequest;
import com.canaiguess.api.exception.DuplicateResourceException;
import com.canaiguess.api.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthenticationController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        ))
@AutoConfigureMockMvc(addFilters = false) // Disables Spring Security filters
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void init() {
    }

    @Test
    public void register_ShouldReturnToken_WhenRequestIsValid() throws Exception {
        RegisterRequest request = new RegisterRequest("john_doe", "john@example.com", "Password123");
        AuthenticationResponse response = AuthenticationResponse.builder().token("mocked-jwt-token").build();

        Mockito.when(authenticationService.register(Mockito.eq(request))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }

    @Test
    void register_ShouldReturn400_WhenInputIsInvalid() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest("", "invalid-email", "123"); // invalid fields

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors").isArray());
    }

    @Test
    void register_ShouldReturn409_WhenUsernameOrEmailExists() throws Exception {
        RegisterRequest request = new RegisterRequest("john_doe", "john@example.com", "Password123");

        Mockito.when(authenticationService.register(Mockito.eq(request)))
                .thenThrow(new DuplicateResourceException("Username is already in use"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Username is already in use"));
    }

    @Test
    void authenticate_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("john_doe", "Password123");
        AuthenticationResponse response = AuthenticationResponse.builder().token("valid-jwt-token").build();

        Mockito.when(authenticationService.authenticate(Mockito.eq(request))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("valid-jwt-token"));
    }

    @Test
    void authenticate_ShouldReturn400_WhenInputIsInvalid() throws Exception {
        AuthenticationRequest invalidRequest = new AuthenticationRequest("", "123"); // invalid fields

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors").isArray());
    }

    @Test
    void authenticate_ShouldReturn401_WhenBadCredentials() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("john_doe", "wrongPassword");

        Mockito.when(authenticationService.authenticate(Mockito.eq(request)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void authenticate_ShouldReturn401_WhenUsernameNotFound() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("unknown_user", "Password123");

        Mockito.when(authenticationService.authenticate(Mockito.eq(request)))
                .thenThrow(new UsernameNotFoundException("Username not found" + request.getUsername()));

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Username not found" + request.getUsername()));
    }
}
