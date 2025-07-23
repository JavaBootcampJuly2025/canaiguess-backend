package com.canaiguess.api.controller;

import com.canaiguess.api.dto.GameDTO;
import com.canaiguess.api.dto.UpdateUserRequestDTO;
import com.canaiguess.api.dto.UserDTO;
import com.canaiguess.api.model.User;
import com.canaiguess.api.service.UserService;
import com.canaiguess.api.service.UserStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
@Tag(name = "User", description = "Endpoints for retrieving user statistics and played games")
public class UserController {

    private final UserStatsService userStatsService;
    private final UserService userService;

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

    @PreAuthorize("#username == authentication.name or hasRole('ADMIN')")
    @PatchMapping("/{username}/update")
    @Operation(summary = "Update password or email")
    public ResponseEntity<Void> updatePassword(@PathVariable String username,
                                               @RequestBody UpdateUserRequestDTO dto,
                                               @AuthenticationPrincipal User actingUser) {
        userService.updateUserByUsername(actingUser, username, dto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("#username == authentication.name or hasRole('ADMIN')")
    @DeleteMapping("/{username}/delete")
    @Operation(summary = "Delete user", description = "Deletes your account or another user (ADMIN only)")
    public ResponseEntity<Void> deleteAccount(@PathVariable String username,
                                              @AuthenticationPrincipal User actingUser) {
        userService.deleteByUsername(actingUser, username);
        return ResponseEntity.noContent().build();
    }



}
