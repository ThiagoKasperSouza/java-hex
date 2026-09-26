package com.dev.news.main.infrastructure.security;

import com.dev.news.main.domain.user.model.Role;
import com.dev.news.main.domain.user.model.User;
import com.dev.news.main.domain.user.ports.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Garante a existência de um usuário ADMIN na inicialização, usando o
 * PasswordEncoder real (BCrypt). As credenciais vêm de app.admin.* (env).
 */
@Component
public class AdminUserBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public AdminUserBootstrap(UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              @Value("${app.admin.username}") String adminUsername,
                              @Value("${app.admin.password}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            userRepository.save(new User(
                    null,
                    adminUsername,
                    passwordEncoder.encode(adminPassword),
                    Role.ADMIN
            ));
        }
    }
}
