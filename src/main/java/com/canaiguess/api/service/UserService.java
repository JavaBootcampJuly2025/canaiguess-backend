package com.canaiguess.api.service;

import com.canaiguess.api.dto.UpdateUserRequestDTO;
import com.canaiguess.api.exception.BusinessErrorCodes;
import com.canaiguess.api.exception.DuplicateResourceException;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void updateUserByUsername(User actingUser, String targetUsername, UpdateUserRequestDTO dto) {
        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + targetUsername));

        // double-check permission
        if (!actingUser.isAdmin() && !actingUser.getUsername().equals(targetUsername)) {
            throw new SecurityException("Unauthorized to update this user");
        }

        if (dto.getCurrentPassword() != null && !passwordEncoder.matches(dto.getCurrentPassword(), targetUser.getPassword())) {
            throw new RuntimeException("Password do not match");
        }

        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            targetUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        if (dto.getEmail() != null && !dto.getEmail().equals(targetUser.getEmail())) {
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new DuplicateResourceException(BusinessErrorCodes.RESOURCE_ALREADY_IN_USE.getDescription());
            }
            targetUser.setEmail(dto.getEmail());
        }

        userRepository.save(targetUser);
    }

    public void deleteByUsername(User actingUser, String username) {
        // double-check permission
        if (!actingUser.isAdmin() && !actingUser.getUsername().equals(username)) {
            throw new SecurityException("Unauthorized to delete this user");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        userRepository.delete(user);
    }

}
