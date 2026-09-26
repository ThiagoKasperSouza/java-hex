package com.dev.news.main.domain.user.model;

/**
 * Papel (role) do usuário, encapsulado em um tipo próprio em vez de uma string solta.
 * Centraliza o nome da authority, a descrição e o parsing seguro.
 */
public enum Role {
    ADMIN("ROLE_ADMIN", "Administrador"),
    USER("ROLE_USER", "Usuário comum");

    private final String authority;
    private final String description;

    Role(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }

    /** Authority usada pelo Spring Security (ex.: "ROLE_ADMIN"). */
    public String authority() {
        return authority;
    }

    public String description() {
        return description;
    }

    /** Converte um nome (case-insensitive) para o enum, validando entrada. */
    public static Role fromName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("O papel (role) não pode ser vazio.");
        }
        try {
            return Role.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Papel (role) desconhecido: " + name);
        }
    }
}
