package com.dev.news.main.domain.user.model;

/**
 * Entidade de domínio do usuário. O campo {@code password} armazena o hash
 * (nunca a senha em texto puro). As regras de negócio básicas são aplicadas
 * no construtor compacto.
 */
public record User(Long id, String username, String password, Role role) {

    public User {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("O nome de usuário não pode ser vazio.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("A senha não pode ser vazia.");
        }
        if (role == null) {
            throw new IllegalArgumentException("O papel (role) é obrigatório.");
        }
    }
}
