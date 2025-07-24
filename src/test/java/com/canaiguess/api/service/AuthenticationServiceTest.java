package com.canaiguess.api.service;

import com.canaiguess.api.dto.AuthenticationRequest;
import com.canaiguess.api.dto.AuthenticationResponse;
import com.canaiguess.api.dto.RegisterRequest;
import com.canaiguess.api.enums.Role;
import com.canaiguess.api.exception.DuplicateResourceException;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        authenticationManager = mock(AuthenticationManager.class);
        authenticationService = new AuthenticationService(
                userRepository, passwordEncoder, jwtService, authenticationManager
        );
    }

    @Test
    void register_shouldSucceed_whenUsernameAndEmailAreUnique() {
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("Password1")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty()); // Optional.empty() represents missing user
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password1")).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(User.class))).thenReturn("mocked-jwt-token");

        AuthenticationResponse response = authenticationService.register(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());

        // Checks if user was created correctly
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("testuser", userCaptor.getValue().getUsername());
    }

    @Test
    void register_shouldThrowException_whenUsernameExists() {
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("Password1")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(new User())); // user exists

        assertThrows(DuplicateResourceException.class, () -> authenticationService.register(request));
    }

    @Test
    void register_shouldThrowException_whenEmailExists() {
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("Password1")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(DuplicateResourceException.class, () -> authenticationService.register(request));
    }

    @Test
    void authenticate_shouldSucceed_whenCredentialsAreValid() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("testuser")
                .password("Password1")
                .build();

        User user = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("mocked-jwt-token");

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("USER", response.getRole());

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("testuser", "Password1")
        );
    }

    @Test
    void authenticate_shouldThrowException_whenUsernameNotFound() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("nonexistent")
                .password("Password1")
                .build();

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authenticationService.authenticate(request));
    }
}