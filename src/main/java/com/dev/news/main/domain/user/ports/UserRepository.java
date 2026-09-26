package com.dev.news.main.domain.user.ports;

import com.dev.news.main.domain.user.model.User;

import java.util.Optional;

/** Porta de saída de persistência de usuários. */
public interface UserRepository {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    boolean existsByUsername(String username);
    User save(User user);
}
