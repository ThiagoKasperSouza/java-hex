package com.dev.news.main.domain.user.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("Já existe um usuário cadastrado com o nome: " + username);
    }
}
