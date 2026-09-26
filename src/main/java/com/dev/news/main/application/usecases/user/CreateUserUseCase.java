package com.dev.news.main.application.usecases.user;

import com.dev.news.main.domain.user.exceptions.UsernameAlreadyExistsException;
import com.dev.news.main.domain.user.model.Role;
import com.dev.news.main.domain.user.model.User;
import com.dev.news.main.domain.user.ports.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Cadastro de usuário. Como o endpoint é público, o papel é sempre USER:
 * um usuário não pode se auto-cadastrar como ADMIN.
 */
public record CreateUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {

    public User execute(String username, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }

        User user = new User(
                null,
                username,
                passwordEncoder.encode(rawPassword),
                Role.USER
        );
        return userRepository.save(user);
    }
}
