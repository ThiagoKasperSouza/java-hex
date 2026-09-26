package com.dev.news.main.application.usecases.user;

import com.dev.news.main.domain.user.exceptions.InvalidCredentialsException;
import com.dev.news.main.domain.user.model.User;
import com.dev.news.main.domain.user.ports.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Autentica o usuário comparando a senha informada com o hash armazenado.
 * Retorna o usuário autenticado; a geração do JWT fica a cargo da camada web.
 */
public record LoginUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {

    public User execute(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Usuário ou senha inválidos."));

        if (!passwordEncoder.matches(rawPassword, user.password())) {
            throw new InvalidCredentialsException("Usuário ou senha inválidos.");
        }
        return user;
    }
}
