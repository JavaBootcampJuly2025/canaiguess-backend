package com.canaiguess.api.config;

import com.canaiguess.api.enums.Role;
import com.canaiguess.api.model.User;
import com.canaiguess.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default.password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner setupAdminUser() {
        return args -> {
            String adminEmail = "admin@canaiguess.com";

            boolean exists = userRepository.findByEmail(adminEmail).isPresent();
            if (!exists) {
                User admin = User.builder()
                        .username("adminadmin123")
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .role(Role.ADMIN)
                        .score(0)
                        .totalGuesses(0)
                        .correctGuesses(0)
                        .build();

                userRepository.save(admin);
            }
        };
    }
}
